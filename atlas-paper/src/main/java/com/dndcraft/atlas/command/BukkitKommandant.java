package com.dndcraft.atlas.command;

import com.dndcraft.atlas.command.brigadier.Kommandant;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;

public class BukkitKommandant extends Kommandant {

	public BukkitKommandant(AtlasCommand built) {
		super(built);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected RequiredArgumentBuilder makeBuilderWithSuggests(String name, ArgumentType<?> type, CmdArg<?> arg) {
		var builder = RequiredArgumentBuilder.argument(name, type);
		if(arg.hasCustomCompleter()) {
			SuggestionProvider provider = new AtlasSuggestionProvider<>(arg);
			builder.suggests(provider);
		}
		return builder;
	}

}
