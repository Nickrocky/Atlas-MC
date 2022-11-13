package com.dndcraft.atlas.util.exception;

public class AtlasLocationConversionException extends Exception{
    public AtlasLocationConversionException(){
        super("Atlas Location failed to be converted to Bukkit Location, Null World provided!");
    }
}
