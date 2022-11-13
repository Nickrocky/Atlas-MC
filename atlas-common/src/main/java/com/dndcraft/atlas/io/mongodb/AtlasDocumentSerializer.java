package com.dndcraft.atlas.io.mongodb;

import com.dndcraft.atlas.io.mongodb.exceptions.AtlasSerializationException;
import org.bson.Document;

public abstract class AtlasDocumentSerializer {

    public abstract Document toDocument(Object o) throws AtlasSerializationException;
}
