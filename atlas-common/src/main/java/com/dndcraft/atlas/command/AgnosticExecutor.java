package com.dndcraft.atlas.command;

import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.agnostic.Sender;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AgnosticExecutor {
	private final AtlasCommand rootCommand;
	
	public boolean onCommand(Sender sender, String label, String[] args) {
		List<String> listArgs = new ArrayList<>();
		for (String arg : args) listArgs.add(arg);
		runCommand(sender, rootCommand, label, listArgs);
		return true;
	}
	
	public List<String> onTabComplete(Sender sender, String alias, String[] args) {
		try{
            List<String> listArgs = new ArrayList<>();
			for (String arg : args) listArgs.add(arg);
			return getCompletions(sender, rootCommand, listArgs);
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			return Lists.newArrayList();
		}
	}
	
	private List<String> getCompletions(Sender sender, AtlasCommand command, List<String> args) {
		AtlasCommand subCommand = wantsSubCommand(command, args);
		if(subCommand != null && subCommand.hasPermission(sender)) {
			args.remove(0);
			return getCompletions(sender, subCommand, args);
		} else {
			List<String> options;
			if(args.isEmpty()) return Lists.newArrayList();
			int index = args.size() - 1;
			String last = args.get(index).toLowerCase();
			if(args.size() == 1) options = subCompletions(sender, command, last);
			else options = new ArrayList<>();
			
			if(index < command.getArgs().size())
				command.getArgs()
				.get(index)
				.getCompleter()
				.suggest(sender, args.get(index))
				.forEach(sugg->options.add(sugg.getLiteral()));
			
			return options.stream().filter(s->s.toLowerCase().startsWith(last)).collect(Collectors.toList());
		}
	}

	private void runCommand(Sender sender, AtlasCommand command, String usedAlias, List<String> args) {
		AtlasCommand subCommand = wantsSubCommand(command, args);
		Atlas.get().getLogger().warning("catching alias " + usedAlias + ". SubCommand found: " + subCommand);
		if(!args.isEmpty()) Atlas.get().getLogger().warning("These are its arguments: " + StringUtils.join(args, ", "));
		if(subCommand != null) {
			runSubCommand(sender, subCommand, usedAlias, args);
		} else if (!command.hasPermission(sender)) {
			sender.sendMessage(RanCommand.ERROR_PREFIX + "You do not have permission to use this");
		} else {
			RanCommand c = new RanCommand(command, usedAlias, sender);
			
			try{
				c.parseAll(args);
			} catch(Exception e) {
				c.handleException(e);
				return;
			}
			
			HelpCommand help = command.getHelp();
			if(help != null && c.hasFlag("h")) {
				help.runHelp(c, c.getFlag("h"));
			} else {
				executeCommand(command, c);
			}
		}
	}
	
	private void runSubCommand(Sender sender, AtlasCommand subCommand, String usedAlias, List<String> args) {
		if(subCommand.isInvokeOverload()) {
			runCommand(sender, subCommand, usedAlias, args);
		} else {
			String usedSubcommandAlias = args.remove(0).toLowerCase();
			String newAlias = usedAlias + ' ' + usedSubcommandAlias;
			runCommand(sender, subCommand, newAlias, args);
		}
	}
	
	private void executeCommand(AtlasCommand command, RanCommand c) {
		try {
			command.execute(c); //TODO will this respect permissions even with invoke()??
		} catch(RanCommand.CmdParserException e) {
			c.handleException(e);
		}
	}
	
	private List<String> subCompletions(Sender sender, AtlasCommand cmd, String argZero){
		List<String> result = new ArrayList<>();
		String lower = argZero.toLowerCase();
		cmd.getSubCommands().stream()
			.filter(s->s.hasPermission(sender))
			.map(s->s.getBestAlias(lower))
			.filter(Objects::nonNull)
			.forEach(result::add);

		return result;
	}
	
	private AtlasCommand wantsSubCommand(AtlasCommand cmd, List<String> args) {
		
		List<AtlasCommand> matches = new ArrayList<>();

		//Check if one of the overloads matches better based on arg count
		cmd.getSubCommands().stream()
		  .filter(AtlasCommand::isInvokeOverload)
		  .filter(ac->ac.fitsArgSize(args.size()))
		  .forEach(matches::add);
		
		if(matches.size() > 1) throw new IllegalStateException("Invoke overload ambiguity: Same arg size took multiple args!!");
		else if(matches.size() == 1) return matches.get(0);
			
		//Otherwise, Check for literals that match a subcommand
		if(args.isEmpty()) return null;
		String subArg = args.get(0).toLowerCase();
		
		cmd.getSubCommands().stream()
			.filter(s->!s.getMainCommand().isEmpty())
		  .filter(s->s.isAlias(subArg))
		  .forEach(matches::add);
		if(matches.isEmpty()) return null;
		else if(matches.size() == 1) return matches.get(0);
		
		int s = args.size() - 1;
		for(AtlasCommand match : matches) {
			if(match.fitsArgSize(s)) return match;
		}
		
		//Fallback, no subcommand wants the amount of given arguments
		return null;
	}


	
}
