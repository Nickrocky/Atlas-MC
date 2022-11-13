package com.dndcraft.atlas.command;

import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.command.brigadier.TooltipProvider;
import com.dndcraft.atlas.util.TimeUtil;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Accessors(fluent= true)
public class ArgBuilder {
	//Either one of these is set, depending on what kind of arg
	private final AtlasCommandBuilder command;
	@Getter(AccessLevel.PACKAGE) private final CmdFlag flag;

	@Setter private String defaultInput;
	@Setter private String name = null;
	@Setter private Component errorMessage = null;
	@Setter private String description = null;
	
	ArgBuilder(AtlasCommandBuilder command) {
		this(command, null);
	}
	
	ArgBuilder(AtlasCommandBuilder command, CmdFlag flag) {
		this.command = command;
		this.flag = flag;
	}

	public AtlasCommandBuilder asInt(){
		asIntInternal();
		return command;
	}
	
	public AtlasCommandBuilder asInt(int min){
		defaults("#","Must be a valid integer of %d or higher", min);
		val arg = asIntInternal();
		arg.setFilter(i->i>=min);
		arg.setBrigadierType(IntegerArgumentType.integer(min));
		return command;
	}
	
	public AtlasCommandBuilder asInt(int min, int max){
		defaults("#","Must be a valid integer between %d and %d", min, max);
		val arg = asIntInternal();
		arg.setFilter(i->(i>=min && i <= max));
		arg.setBrigadierType(IntegerArgumentType.integer(min, max));
		return command;
	}
	
	public AtlasCommandBuilder asInt(IntPredicate filter) {
		val arg = asIntInternal();
		arg.setFilter(i->filter.test(i));
		return command;
	}
	
	
	private CmdArg<Integer> asIntInternal(){
		defaults("#",Component.text("Not an accepted integer"));
		CmdArg<Integer> arg = build(Integer.class);
		arg.setMapper(Ints::tryParse);
		arg.setBrigadierType(IntegerArgumentType.integer());
		return arg;
	}
	
	public AtlasCommandBuilder asLong(){
		asLongInternal();
		return command;
	}
	
	private CmdArg<Long> asLongInternal() {
		defaults("#l",Component.text("Not an accepted longinteger"));
		CmdArg<Long> arg = build(Long.class);
		arg.setMapper(Longs::tryParse);
		return arg;
	}
	
	public AtlasCommandBuilder asDouble() {
		asDoubleInternal();
		return command;
	}
	
	public AtlasCommandBuilder asDouble(double min){
		val arg = asDoubleInternal();
		arg.setFilter(d->d>=min);
		arg.setBrigadierType(DoubleArgumentType.doubleArg(min));
		return command;
	}
	
	public AtlasCommandBuilder asDouble(double min, double max){
		val arg = asDoubleInternal();
		arg.setFilter(d->(d>=min && d <= max));
		arg.setBrigadierType(DoubleArgumentType.doubleArg(min,max));
		return command;
	}
	
	public AtlasCommandBuilder asDouble(DoublePredicate filter) {
		val arg = asDoubleInternal();
		arg.setFilter(i->filter.test(i));
		return command;
	}
	
	private CmdArg<Double> asDoubleInternal(){
		defaults("#.#",Component.text("Not an accepted number"));
		CmdArg<Double> arg = build(Double.class);
		arg.setMapper(Doubles::tryParse);
		arg.setBrigadierType(DoubleArgumentType.doubleArg());
		return arg;
	}
	
	public AtlasCommandBuilder asFloat() {
		asFloatInternal();
		return command;
	}
	
	public AtlasCommandBuilder asFloat(float min) {
		val arg = asFloatInternal();
		arg.setFilter(d->d>=min);
		arg.setBrigadierType(FloatArgumentType.floatArg(min));
		return command;
	}
	
	public AtlasCommandBuilder asFloat(float min, float max) {
		val arg = asFloatInternal();
		arg.setFilter(d->(d>=min && d <= max));
		arg.setBrigadierType(FloatArgumentType.floatArg(min, max));
		return command;
	}
	
	public CmdArg<Float> asFloatInternal() {
		defaults("#.#",Component.text("Not an accepted number"));
		CmdArg<Float> arg = build(Float.class);
		arg.setMapper(Floats::tryParse);
		arg.setBrigadierType(FloatArgumentType.floatArg());
		return arg;
	}
	
	public AtlasCommandBuilder asString(){
		defaults("*",Component.text("Provide an argument"));
		val arg = build(String.class);
		arg.setMapper($->$);
		return command;
	}
	
	public AtlasCommandBuilder asString(String... options){
		defaults("*",Component.text("Must be one of these: " + StringUtils.join(options, ", ")));
		val arg = build(String.class);
		arg.setFilter( s-> Stream.of(options).filter(s2->s2.equalsIgnoreCase(s)).findAny().isPresent() );
		arg.setMapper($->$);
		arg.completeMe(options);
		return command;
	}
	
	public AtlasCommandBuilder asStringArray() {
		if(flag != null) throw new IllegalStateException("Cannot use joined arguments for parameters/flags");
		
		defaults("args", Component.text("Provide multiple arguments"));
		ArrayArgs arg = new ArrayArgs(name, errorMessage, defaultInput, description);
		command.noMoreArgs = true;
		command.addArg(arg);
		return command;
	}
	
	
	public <T extends Enum<T>> AtlasCommandBuilder asEnum(Class<T> clazz) {
		defaults(clazz.getSimpleName(),Component.text("Not a valid " + clazz.getSimpleName()));
		val arg = build(clazz);
		arg.setMapper(s->{
			try{ return Enum.valueOf(clazz, s.toUpperCase()); }
			catch(IllegalArgumentException e) {return null;}
		});
		
		arg.setCompleter( CommandCompleter.suggestWithTooltips( ()-> enumCompleter(clazz)  ));
		return command;
	}
	
	private <T extends Enum<T>> List<CommandCompleter.Suggestion> enumCompleter(Class<T> clazz){
		val stream = Stream.of(clazz.getEnumConstants());
		if(TooltipProvider.class.isAssignableFrom(clazz)) {
			return stream.map(e-> new CommandCompleter.Suggestion(e.name().toLowerCase(), ((TooltipProvider)e).getTooltip()))
					.collect( Collectors.toList() );
		} else {
			return stream.map(Enum::name).map(String::toLowerCase).map(CommandCompleter.Suggestion::new).collect(Collectors.toList());
		}
	}
	
	public AtlasCommandBuilder asInstant() {
		defaults("time",Component.text("Please provide a valid date (YYYY-MM-DD), time (hh:mm:ss), datetime (YYYY-MM-DDThh:mm:ss) or duration (e.g.: 3w10d5h7m40s)"));
		val arg = build(Instant.class);
		arg.setMapper(TimeUtil::parseEager);
		return command;
	}
	
	public AtlasCommandBuilder asDuration() {
		defaults("duration",Component.text("Please provide a duration (e.g.: 3w10d5h17m40s)"));
		val arg = build(Duration.class);
		arg.setMapper(TimeUtil::parseDuration);
		return command;
	}
	
	public AtlasCommandBuilder asTimestamp() {
		defaults("timestamp",Component.text("Provide a timestamp in miliseconds"));
		val arg = build(Timestamp.class);
		arg.setMapper(TimeUtil::parseTimestamp);
		return command;
	}
	
	public AtlasCommandBuilder asBoolean() {
		defaults("bool",Component.text("Please provide either true/false."));
		val arg = build(Boolean.class);
		arg.setMapper(s -> {
			if(Stream.of("true","yes","y").anyMatch(s::equalsIgnoreCase)) return true;
			else if(Stream.of("false","no","n").anyMatch(s::equalsIgnoreCase)) return false;
			else return null;
		});
		arg.setBrigadierType(BoolArgumentType.bool());
		return command;
	}
	
	public AtlasCommandBuilder asBoolean(boolean def) {
		this.defaultInput = def? "y":"n";
		return asBoolean();
	}
	
	public AtlasCommandBuilder asVoid() {
		if(flag == null) throw new IllegalStateException("This makes no sense to use for anything but flags");
		VoidArg arg = new VoidArg(name,  errorMessage, description);
		flag.setArg(arg);
		return command;
	}
	
	public AtlasCommandBuilder asJoinedString() {
		if(flag != null) throw new IllegalStateException("Cannot use joined arguments for parameters/flags");
		
		defaults("**", Component.text("Provide any sentence, spaces allowed."));
		JoinedArg arg = new JoinedArg(name, errorMessage, defaultInput, description );
		command.noMoreArgs = true;
		command.addArg(arg);
		return command;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <X> AtlasCommandBuilder asType(Class<X> c) {
		if( c == Void.class) {
			asVoid(); //Flags only
		}else if( c == String.class) {
			asString();
		}else if(c==int.class || c== Integer.class) {
			asInt();
		}else if(c==long.class || c== Long.class) {
			asLong();
		} else if(c== Float.class || c==float.class) {
			asFloat();
		} else if(c== Double.class || c==double.class) {
			asDouble();
		} else if(c== Boolean.class || c==boolean.class) {
			asBoolean();
		} else if (c== String[].class) {
			asStringArray();
		} else if(c== Instant.class) {
			asInstant();
		} else if(c== Timestamp.class) {
			asTimestamp();
		} else if(c== Duration.class) {
			asDuration();
		} else if(c.isEnum() && !ParameterType.argumentTypeExists(c)) {
			asEnum((Class<Enum>) c);
		} else {
			asCustomType(c);
		}
		
		return command;
	}
	
	private <X> AtlasCommandBuilder asCustomType(Class<X> clazz) {
		
		if(!ParameterType.argumentTypeExists(clazz))
			throw new IllegalArgumentException("This class was not registered as a CUSTOM argument type: " + clazz.getSimpleName());
		
		@SuppressWarnings("unchecked")
		ParameterType<X> result = (ParameterType<X>) ParameterType.getCustomType(clazz);
		String defaultName = result.getDefaultName() == null ? clazz.getSimpleName() : result.getDefaultName();
		Component defaultError = result.getDefaultError() == null ? Component.text("Please provide a valid " + clazz.getSimpleName()) : result.getDefaultError();
		defaults(defaultName, defaultError);
		
		CmdArg<X> arg = build(clazz);
		result.settle(arg);
		return command;
	}
	
	private void defaults(String name, String err, Object... formats) {
		if(this.name == null) this.name = name;
		if(errorMessage == null) this.errorMessage = Component.text(String.format(err, formats));
	}

	private void defaults(String name, Component errorMessage){
		if(this.name == null) this.name = name;
		if(errorMessage == null) this.errorMessage = errorMessage;
	}

	private <T> CmdArg<T> build(Class<T> clazz){
		Atlas.get().getLogger().info("Building arg for class: " + clazz.getSimpleName() + " for command: " + command.mainCommand());
		
		CmdArg<T> arg = new CmdArg<>(name, defaultInput, description, errorMessage);
		if(flag == null) command.addArg(arg);
		else flag.setArg(arg);
		return arg;
	}
}
