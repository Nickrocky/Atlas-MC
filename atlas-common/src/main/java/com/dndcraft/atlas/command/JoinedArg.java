package com.dndcraft.atlas.command;

import com.dndcraft.atlas.agnostic.Sender;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.kyori.adventure.text.Component;

import java.util.List;

public class JoinedArg extends CmdArg<String> {

	public JoinedArg(String name, Component errorMessage, String defaultInput, String description) {
		super(name, defaultInput, description, errorMessage);
		this.setMapper(s->s);
		this.setBrigadierType(StringArgumentType.greedyString());
	}

	@Override
    String resolve(Sender s, List<String> input, int i) {
		List<String> relevantInput = input.subList(i, input.size());
		String joined = String.join(" ", relevantInput);
		return resolve(s, joined);
	}
	
}
