package com.dndcraft.atlas.command.exception;

public class InvalidPluginCommandException extends Exception {

    public InvalidPluginCommandException(){
        super("Null Plugin Command! Please ensure that all of your commands are registered in plugin.yml");
    }

}
