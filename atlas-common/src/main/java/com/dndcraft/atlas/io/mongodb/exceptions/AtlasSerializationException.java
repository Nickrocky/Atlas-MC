package com.dndcraft.atlas.io.mongodb.exceptions;

public class AtlasSerializationException extends Exception {

    public AtlasSerializationException(Object o){
        super("Unable to serialize " + o.getClass().getName() + " into BSON");
    }

}
