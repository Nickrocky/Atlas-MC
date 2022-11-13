package com.dndcraft.atlas.command;

import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.agnostic.AbstractComponentBuilder;
import com.dndcraft.atlas.agnostic.Sender;
import com.dndcraft.atlas.util.AtlasColor;
import com.google.common.primitives.Ints;
import lombok.val;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

import java.util.Arrays;
import java.util.Collections;

import java.util.List;
import java.util.stream.Collectors;


public class HelpCommand extends AtlasCommand {
	private static final AtlasColor[] colors = new AtlasColor[] {AtlasColor.AQUA, AtlasColor.DANDELION, AtlasColor.GREEN, AtlasColor.LILAC, AtlasColor.GOLD};
	private final AtlasCommand parent;
	
	HelpCommand(AtlasCommand ac) {
	  super("help",
	  		Collections.emptySet(),
	  		"prints help",
	  		ac.getPermission(),
	  		null,
	  		Arrays.asList(helpPageArg()),
	  		Collections.emptyList(),
	  		Collections.emptyList(),
	  		null);
	  
	  parent = ac;
	  
	}
	
	private static CmdArg<Integer> helpPageArg(){
		val c = new CmdArg<Integer>("page", "not a valid integer", "0", null);
		c.setFilter(i->i>=0);
		c.setMapper(Ints::tryParse);
		return c;
	}
	
	@Override
	void execute(RanCommand c) {
		int page = c.getArg(0);
		runHelp(c, page);
	}
	
	public void runHelp(RanCommand c, int page) {

		if(page > 0) {
			int min = 7 + (page-1)*8;
			if(parent.hasDescription()) min--;
			int max = min + 8;
			outputSubcommands(c, min, max);
		}
		else {
			outputBaseHelp(c);
		}
	}
	
	private void outputBaseHelp(RanCommand c) {
		Sender s = c.getSender();
		var baseHelpComponent = Atlas.get().componentBuilder();
		var permissionSubComponent = Atlas.get().componentBuilder();
		var flagSubComponent = Atlas.get().componentBuilder();
		if(parent.getPermission() != null) baseHelpComponent
				.append("[P]")
				.color(AtlasColor.DANDELION)
				.hoverText(permissionSubComponent
						.append("Permission required: ", AtlasColor.DANDELION)
						.append(parent.getPermission(), AtlasColor.GREEN).build());

		if(!parent.getFlags().isEmpty()) {
			baseHelpComponent.append("[F]", AtlasColor.LILAC);
			flagSubComponent
					.append("Accepted Command Flags:", AtlasColor.DANDELION);
			for (CmdFlag flag : parent.getFlags()) {
				flagSubComponent.newline();
				flagSubComponent.append("-"+flag.getName(), AtlasColor.WHITE);
				String flagPex = flag.getPermission();
				String flagDesc = flag.getArg().getDescription();
				
				if(flagDesc != null) flagSubComponent.append(": ").append(flagDesc, AtlasColor.GRAY);
				if(flagPex != null) flagSubComponent.append(' ').append("("+flagPex+")", AtlasColor.DANDELION);
			}
			baseHelpComponent.hoverText(flagSubComponent.build());
		}
		
		commandHeadline(baseHelpComponent,c).send(s);
		var descriptionComponent = Atlas.get().componentBuilder().append(parent.getDescription(), AtlasColor.GRAY, TextDecoration.ITALIC).build();
		if(parent.hasDescription()) c.msg(descriptionComponent);
		
		int max = parent.hasDescription()? 6:7;
		outputSubcommands(c, 0, max);
	}
	
	private AbstractComponentBuilder<?> commandHeadline(AbstractComponentBuilder<?> b, RanCommand c) {
		String alias = "/" + c.getUsedAlias();
		if(alias.endsWith("help")) alias = alias.substring(0, alias.length() - 5);
		b.append(alias, AtlasColor.GOLD);
		b.onClickSuggestCommand(alias);
		fillArgs(parent, alias, b, true);
		return b;
	}
	
	private void fillArgs(AtlasCommand command, String alias, AbstractComponentBuilder<?> b, boolean useColor) {
		int i = 0;
		for(CmdArg<?> a : command.getArgs()) {
			boolean optional = a.hasDefaultInput();
			b.append(" ");
			var color = AtlasColor.WHITE;
			if(useColor) color = colorCoded(i++);
			b.append(optional? "[":"<", color);
			if(a.hasDescription()) b.hoverText(a.getDescription());
			else b.onClickSuggestCommand(alias);
			b.append(a.getName(), color)
			.append(optional? "]":">", color);
		}
	}

	private AtlasColor colorCoded(int i) {
		return colors[i%colors.length];
	}
	
	void outputSubcommands(RanCommand c, int min, int max) {
		Sender s = c.getSender();
		List<AtlasCommand> subs = parent.getSubCommands().stream()
				.filter(sub->sub!=this)
				.filter(sub->sub.hasPermission(s))
				.sorted((s1,s2)-> s1.getMainCommand().compareTo(s2.getMainCommand()))
				.collect(Collectors.toList());
		
		String alias = "/" + c.getUsedAlias();
		if(alias.endsWith("help")) alias = alias.substring(0, alias.length() - 5);
		
		if(subs.size() <= min) {
			if(min > 0) s.sendMessage(RanCommand.ERROR_PREFIX.append(Component.text("Invalid help page!", AtlasColor.RED.toTextColor())));
			return;
		} else {
			String trailing = alias.substring(alias.lastIndexOf(" ")+1);
			AbstractComponentBuilder<?> b = Atlas.get().componentBuilder().append("-== Possible sub-commands for ", AtlasColor.NAVY_BLUE)
					.append(trailing, AtlasColor.GRAY).append(" ==-", AtlasColor.NAVY_BLUE);
			if(min > 0){
				var hoverText = Atlas.get().componentBuilder().append("Previous Page", AtlasColor.RED).build();
				b.append(" ").appendBracketed('\u2190', AtlasColor.DARK_GRAY, AtlasColor.RED).hoverText(hoverText).onClickRunCommand(alias + " -h " + (min/8));
			}
			if(subs.size() > max){
				var hoverText = Atlas.get().componentBuilder().append("Next Page", AtlasColor.RED).build();
				b.append(" ").appendBracketed('\u2192', AtlasColor.DARK_GRAY, AtlasColor.RED).hoverText(hoverText).onClickRunCommand(alias + " -h " + ((min+2)/8+1));
			}
			
			c.msg(b.build());
		}
		
		//Arrays start at 1 fight me
		for(int i = min; i < max; i++) {
			if(subs.size() <= i) break;
			AtlasCommand sub = subs.get(i);
			String subber = sub.getMainCommand();

			AbstractComponentBuilder b = Atlas.get().componentBuilder().append(subber, AtlasColor.DANDELION);
			if(sub.getHelp() != null) b.onClickRunCommand(alias + ' ' + subber + " -h 0").hoverText("Click for help on this subcommand!");
			else b.onClickSuggestCommand(alias + ' ' + subber + ' ').hoverText("Click to run this command");
			fillArgs(sub, alias + ' ' + subber, b, false);
			
			if(sub.hasDescription()) {
				int room = 57 - PlainComponentSerializer.plain().serialize(b.build()).length();
				if(room > 0)  {
					b.append(": ");
					String desc = sub.getDescription();
					if(desc.length() > room) desc = desc.substring(0, room-1) + '\u2026';
					b.append(desc, AtlasColor.GRAY);
				}
			}
			
			b.send(s);
		}

	}
}
