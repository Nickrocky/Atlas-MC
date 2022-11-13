package com.dndcraft.atlas.command;

import com.dndcraft.atlas.agnostic.Sender;
import com.dndcraft.atlas.wrapper.BukkitSender;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class AtlasSuggestionProvider<T> implements SuggestionProvider<T> {
	private final CmdArg<T> arg;

	@Override
	public CompletableFuture<Suggestions> getSuggestions(CommandContext<T> context, SuggestionsBuilder builder) throws CommandSyntaxException {
		T source = context.getSource();
		Sender sender = new BukkitSender(BrigadierProvider.get().getBukkitSender(source));
		
		for(CommandCompleter.Suggestion suggestion : arg.getCompleter().suggest(sender, builder.getRemaining())) {
			String sugg = suggestion.getLiteral();
			if (sugg.toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
				if(suggestion.hasTooltip()) builder.suggest(sugg, new LiteralMessage(suggestion.getTooltip()) );
				else builder.suggest(sugg);
			}
		}

		return builder.buildFuture();
	}

}
