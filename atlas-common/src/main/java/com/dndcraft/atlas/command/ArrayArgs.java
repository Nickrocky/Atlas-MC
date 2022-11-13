package com.dndcraft.atlas.command;

import com.dndcraft.atlas.agnostic.Sender;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.kyori.adventure.text.Component;

import java.util.List;

public class ArrayArgs extends CmdArg<String[]> {

	public ArrayArgs(String name, Component errorMessage, String defaultInput, String description) {
		super(name,  defaultInput, description, errorMessage);
		this.setMapper(s->s.split(" "));
		this.setBrigadierType(StringArgumentType.greedyString());
	}

	//Goes from a list of strings to a single string joined by whitespace
	//Which is then turned into a String array. Not my proudest work
	
	@Override
	String[] resolve(Sender s, List<String> input, int i) {
		List<String> relevantInput = input.subList(i, input.size());
		String joined = String.join(" ", relevantInput);
		return resolve(s, joined);
	}
	

}
