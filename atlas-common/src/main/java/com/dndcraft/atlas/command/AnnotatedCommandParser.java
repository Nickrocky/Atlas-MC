package com.dndcraft.atlas.command;

import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.agnostic.Command;
import com.dndcraft.atlas.agnostic.Sender;
import com.dndcraft.atlas.command.annotations.*;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class AnnotatedCommandParser {
	private final Supplier<CommandTemplate> template;
	private final Command pluginCommand;
	
	public AtlasCommandBuilder invokeParse(Consumer<AtlasCommand> registrationHandler) {
		AtlasCommandBuilder acb = new AtlasCommandBuilder(registrationHandler, pluginCommand);
		return parse(template, acb);
	}
	
	private AtlasCommandBuilder parse(Supplier<CommandTemplate> template, AtlasCommandBuilder acb) {
		Class<? extends CommandTemplate> c = template.get().getClass();
		
		addInvoke(c, template, acb);
		
		var cmds = Stream.of(c.getMethods()).filter(m->m.isAnnotationPresent(Cmd.class)).collect(Collectors.toList());
		
		for(Method method : cmds) checkForSubLayer(method, template, acb); //Note this recurses
		for(Method method : cmds) parseCommand(method, template, acb);
		
		return acb; //this::parse is recursive. So is ArcheCommandBuilder::build. Perfect synergy :)
	}
	
	private void addInvoke(Class<? extends CommandTemplate> c, Supplier<CommandTemplate> template, AtlasCommandBuilder acb) {
		//This breaks polymorphism but whatever
		boolean firstInvokeFound = false;
		for(Method m : c.getDeclaredMethods()) {
			if(m.getParameterCount() > 0 && Modifier.isPublic(m.getModifiers()) && m.getName().equals("invoke") && m.getReturnType() == Void.TYPE) {
				//This is a invoke method declared in the class, assumed this is what we want for the default invocation of the command
				//Due to logic of the ArcheCommandExecutor this still makes the no-argument default a help command
				if(!firstInvokeFound) {
					parseCommandMethod(m, template, acb);
					firstInvokeFound = true;
				} else {
					var overload = acb.overloadInvoke();
					parseCommandMethod(m, template, overload);
					overload.build();
				}
			}
		}
		
		//Fallback option, which does use polymorphism, specifically the CommandTemplate.invoke() method
		if(!firstInvokeFound) acb.payload(rc->{ //This is default behavior when no arguments are given, usually refers to help file
			CommandTemplate t = template.get();
			t.setRanCommand(rc);
			t.invoke();
		});
	}
	
	private AtlasCommandBuilder constructSubBuilder(Method method, AtlasCommandBuilder parent) {
		String name = method.getName();
		
		Cmd anno = method.getAnnotation(Cmd.class);
		if(!anno.alias().isEmpty()) name = anno.alias();
			
		String desc = anno.value();
		String pex = anno.permission();
		boolean flags = anno.flags();
		
		var result = parent.subCommand(name, false);
		
		result.alias(anno.aliases());
		if(!flags) result.noFlags();
		if(desc !=  null) result.description(desc);
		if(StringUtils.isNotEmpty(pex)) result.permission(pex);
		
		return result;
	}
	
	@SneakyThrows
	private void checkForSubLayer(Method method, Supplier<CommandTemplate> template, AtlasCommandBuilder acb) {
		if(!CommandTemplate.class.isAssignableFrom(method.getReturnType())) return;
		if(method.getParameterCount() > 0) throw new IllegalStateException("Methods returning CommandTemplate can't also have parameters");
		if(method.getName().equals("invoke")) throw new IllegalArgumentException("Don't annotate your invoke() methods. The method name is reserved!");
		
		AtlasCommandBuilder subbo = constructSubBuilder(method, acb);
		Supplier<CommandTemplate> chained = ()-> chainSupplier(method, template);
		parse(chained, subbo).build(); //We need to go deeper
	}
	
	@SneakyThrows
	private CommandTemplate chainSupplier(Method templateGetter, Supplier<CommandTemplate> theOldSupplier) {
		//Makes a NEW supplier which invokes the old supplier (which is one higher in the chain)
		//The supplied CommandTemplate has a particular method called via reflection
		//A method which we know to return CommandTemplate (checked above), so we cast it
		//This supplier is then used for subsequent checking.
		//Yes this is an abysmal piece of code. Let's never speak of it.
		return (CommandTemplate) templateGetter.invoke(theOldSupplier.get());
	}
	
	private void parseCommand(Method method, Supplier<CommandTemplate> template, AtlasCommandBuilder acb) {
		if(method.getReturnType() != Void.TYPE) return;
		if(method.getName().equals("invoke")) throw new IllegalArgumentException("Don't annotate your invoke() methods. The method name is reserved!");
		
		var subbo = constructSubBuilder(method, acb);
		parseCommandMethod(method, template, subbo);
		subbo.build();
	}

	private void parseCommandMethod(Method method, Supplier<CommandTemplate> template, AtlasCommandBuilder acb) {
		var flagsAnno = method.getAnnotation(Flag.List.class);
		if(flagsAnno != null) for(Flag flag : flagsAnno.value()) addFlag(acb, flag);
		else if(method.isAnnotationPresent(Flag.class)) addFlag(acb, method.getAnnotation(Flag.class));
		
		var params = method.getParameters();
		
		boolean wantsSenderAsFirstArg = false;
		for (int i = 0; i < params.length; i++) {
			var param = params[i];
			var c = param.getType();
			Atlas.get().getLogger().info("Param " + i + " in method " + method.getName() + " has type " + c);
			
			if(i == 0) {
				//The first argument MIGHT be a sender argument, and often is
				//but it does not necessarily NEED to be... thus we check
				//If a SenderTemplate is registered, we go forward
				if(ParameterType.senderTypeExists(c)) {
					Atlas.get().getLogger().info("Method " + method.getName() + " for cmd " + acb.mainCommand() +
							" wants sender type: " + ParameterType.getCustomType(c).getTargetType());
					acb.requiresSender(c);
					continue;
				} else if( Sender.class.isAssignableFrom(c)) {
					wantsSenderAsFirstArg = true;
					continue;
				}
			}
			
			var argAnno = param.getAnnotation(Arg.class);
			ArgBuilder arg = argAnno == null? acb.arg() : acb.arg(argAnno.value());
			if(argAnno != null && !argAnno.description().isEmpty()) arg.description(argAnno.description());
			
			Default defaultInput = param.getAnnotation(Default.class);
			if(defaultInput != null) {
				String def = defaultInput.value();
				Validate.notNull(def);
				arg.defaultInput(def);
			}
			
			if(param.isAnnotationPresent(Joined.class)) {
				if(param.getType() == String.class) arg.asJoinedString();
				else throw new IllegalArgumentException("All JoinedString annotations must affect a String type parameter");
			} else if (param.isAnnotationPresent(Range.class)) {
				Range rangeInput = param.getAnnotation(Range.class);
				//Retarded magical numbers bs is here. Sorry.
				boolean hasMin = rangeInput.min() != Integer.MIN_VALUE;
				boolean hasMax = rangeInput.max() != Integer.MAX_VALUE;
				if(c == int.class || c == Integer.class) {
					if(hasMin && hasMax) arg.asInt((int)rangeInput.min(), (int)rangeInput.max());
					else if(hasMin && !hasMax) arg.asInt((int) rangeInput.min());
					else throw new IllegalArgumentException("Use @Range by specifying either a min or a min and max");
				} else if(c == float.class || c == Float.class) {
					if(hasMin && hasMax) arg.asFloat((float)rangeInput.min(), (float) rangeInput.max());
					else if(hasMin && !hasMax) arg.asFloat((float) rangeInput.min());
					else throw new IllegalArgumentException("Use @Range by specifying either a min or a min and max");
				} else if(c == double.class || c == Double.class) {
					if(hasMin && hasMax) arg.asDouble(rangeInput.min(), rangeInput.max());
					else if(hasMin && !hasMax) arg.asDouble( rangeInput.min());
					else throw new IllegalArgumentException("Use @Range by specifying either a min or a min and max");
				} else {
					throw new IllegalArgumentException("Use @Range annotation only on integer, float or double!");
				}
			} else {
				arg.asType(c);
			}
		}
		
		makeCommandDoStuff(template, acb, method, wantsSenderAsFirstArg);
	}

	private void makeCommandDoStuff(Supplier<CommandTemplate> template, AtlasCommandBuilder acb, Method method, boolean wantsCommandSenderAsFirstArg) {
		//Make command actually do stuff
		acb.payload(rc->{
			try {
				CommandTemplate t = template.get();
				t.setRanCommand(rc);
				Object[] args = rc.getArgResults().toArray();

				if(acb.requiresSender()) {
					Object[] newArgs = insertFirst(args, rc.getResolvedSender());
					method.invoke(t, newArgs);
				} else if (wantsCommandSenderAsFirstArg) {
					Object[] newArgs = insertFirst(args, rc.getSender());
					method.invoke(t, newArgs);
				}else {
					method.invoke(t, args);
				}
			} catch (InvocationTargetException ite) {
				if(ite.getCause() instanceof RanCommand.CmdParserException) {
					rc.error(Component.text().content(ite.getCause().getMessage()).asComponent());
				} else {
					ite.printStackTrace();
					rc.error(Component.text().content("An unhandled exception occurred. Contact a developer.").asComponent());
				}
			} catch (Exception e) {
				e.printStackTrace();
				rc.error(Component.text().content("An unhandled exception occurred. Contact a developer.").asComponent());
			}
		});
	}

	
	private Object[] insertFirst(Object[] args, Object toAdd) {
		Object[] newArgs = new Object[args.length+1];
		System.arraycopy(args, 0, newArgs, 1, args.length);
		newArgs[0] = toAdd;
		return newArgs;
	}
	
	private void addFlag(AtlasCommandBuilder acb, Flag flag) {
		ArgBuilder flarg;
		
		String pex = flag.permission();
		if(!pex.isEmpty()) flarg = acb.restrictedFlag(flag.name(), pex, flag.aliases());
		else flarg = acb.flag(flag.name(), flag.aliases());
		
		String desc = flag.description();
		if(!desc.isEmpty()) flarg.description(desc);

		flarg.asType(flag.type());
	}
}
