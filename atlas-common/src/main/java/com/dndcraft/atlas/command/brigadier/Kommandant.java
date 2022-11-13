package com.dndcraft.atlas.command.brigadier;

import com.dndcraft.atlas.command.AtlasCommand;
import com.dndcraft.atlas.command.CmdArg;
import com.dndcraft.atlas.command.CmdFlag;
import com.dndcraft.atlas.command.HelpCommand;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class Kommandant {
	private final AtlasCommand head;
	private final List<CommandNode<Object>> rootNodes = new ArrayList<>();

	public void addBrigadier() {
		rootNodes.add(buildNode(head, null));
	}

	public List<CommandNode<Object>> getNodes(){
		return Collections.unmodifiableList(rootNodes);
	}

	private CommandNode<Object> buildNode(AtlasCommand cmd, CommandNode<Object> dad) {
		CommandNode<Object> node = null;

		if(cmd.isInvokeOverload()) {
			node = dad;
		} else {
			val builder = LiteralArgumentBuilder.literal(cmd.getMainCommand());
			if(!cmd.hasArgs() && !cmd.isEmptyCommand()) builder.executes($->0);
			node = builder.build();
		}

		for(var sub : cmd.getSubCommands()) {
			if(sub instanceof HelpCommand) continue;
			var subNode = buildNode(sub, node); //Recurses
			if(!sub.isInvokeOverload()) node.addChild(subNode);
		}

		CommandNode<Object> argument = node;
		Map<String, Integer> namesUsed = new HashMap<>();
		List<CmdFlag> flags = cmd.getFlags().stream().filter(flag->!(flag.getName().equals("sudo") || flag.getName().equals("h"))).collect(Collectors.toList());
		var queue = new LinkedList<>(cmd.getArgs());
		if (queue.isEmpty()) addFlags(flags, node); // case zero args, add to node
		while(!queue.isEmpty()) {
			var arg = queue.poll();
			var next = queue.peek();
			boolean executes = next == null || next.hasDefaultInput();
			
			//Adds numbers to duplicate names: prevents crashes and gray-text rubbish
			String name = arg.getName();
			Integer value = namesUsed.compute(name, (k, v)->v==null? 1 : v+1);
			if(value > 1) name = name+value;

			CommandNode<Object> nextArg = buildNodeForArg(name, arg, executes);

			argument.addChild(nextArg);
			
			if (executes) addFlags(flags, nextArg); // otherwise, add flags to last node
			
			//TODO add flags as an option for final arguments that have a default input; will need to pass to the RequiredArgumentBuilder
			// perhaps have the suggestions only appear when a '-' is explicitly entered if possible
			
			argument = nextArg;
			
		}

		redirectAliases(cmd, dad, node);
		return node;
	}
	
	private void addFlags(List<CmdFlag> flags, CommandNode<Object> node) {
		CommandNode<Object> lastFlag = null;
		if (flags.size() > 0) {
			for (int i = 0 ; i < flags.size() ; i++) {
				CmdFlag flag = flags.get(i);
				CommandNode<Object> flagArg = makeFlagBuilder(flags);

				if(lastFlag == null) node.addChild(flagArg);
				else lastFlag.addChild(flagArg);

				lastFlag = flagArg;

				if (!flag.isVoid()) {
					flagArg = makeFlagBuilder(flags); //TODO make provider for flag arguments
					lastFlag.addChild(flagArg);
					lastFlag = flagArg;
				}
			}
		}
	}

	//TODO improve flag completion to include subclass permission checks
	private CommandNode<Object> makeFlagBuilder(List<CmdFlag> flags) {
		var builder = RequiredArgumentBuilder.argument("flags", StringArgumentType.word());
		builder.executes( $->0 );
		builder.suggests(new FlagSuggestionProvider<>(flags));
		return builder.build();
	}

	@RequiredArgsConstructor
	public class FlagSuggestionProvider<T> implements SuggestionProvider<T> {

		private final List<CmdFlag> flags;

		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<T> context, SuggestionsBuilder builder)
				throws CommandSyntaxException {
			for (CmdFlag flag : flags) {
				builder.suggest("-" + flag.getName());
			}
			return builder.buildFuture();
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private CommandNode<Object> buildNodeForArg(String name, CmdArg<?> arg, boolean executes){

		ArgumentType argumentType = arg.getBrigadierType();

		var builder = makeBuilderWithSuggests(name, argumentType, arg);
		if(executes) builder.executes( $->0 );
		return builder.build();
	}
	
	

	@SuppressWarnings("rawtypes") //Any of the Brigadier generic types are never important.
	protected abstract RequiredArgumentBuilder makeBuilderWithSuggests(String name, ArgumentType<?> type, CmdArg<?>arg);

	private void redirectAliases(AtlasCommand cmd, CommandNode<Object> parent, CommandNode<Object> theOneTrueNode) {
		for(String alias : cmd.getAliases()) {
			if(alias.equalsIgnoreCase(cmd.getMainCommand())) continue;
			var node = LiteralArgumentBuilder.literal(alias).redirect(theOneTrueNode).build();
			if(parent == null) {
				rootNodes.add(node);
			}
			else parent.addChild(node);
		}
	}
}