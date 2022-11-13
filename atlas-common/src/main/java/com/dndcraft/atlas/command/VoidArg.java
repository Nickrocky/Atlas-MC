package com.dndcraft.atlas.command;

import net.kyori.adventure.text.Component;

public class VoidArg extends CmdArg<Void> { //If this actually makes it through debugging please punch me in the face
	
	public VoidArg(String name, Component errorMessage, String description) {
		super(name,  "PLACEHOLDER_IF_YOU_SEE_THIS_SOMETHING_WENT_WRONG", description, errorMessage);
		this.setMapper(s->null);
	}

}
