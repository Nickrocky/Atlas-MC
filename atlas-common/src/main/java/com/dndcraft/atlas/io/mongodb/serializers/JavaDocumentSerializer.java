package com.dndcraft.atlas.io.mongodb.serializers;

import com.dndcraft.atlas.io.mongodb.AtlasDocumentSerializer;
import com.dndcraft.atlas.io.mongodb.exceptions.AtlasSerializationException;
import org.bson.Document;

import java.time.LocalDateTime;

public class JavaDocumentSerializer extends AtlasDocumentSerializer {
    @Override
    public Document toDocument(Object o) throws AtlasSerializationException {
        Document document = null;
        if(o instanceof LocalDateTime){
            LocalDateTime l = (LocalDateTime) o;
            document = new Document()
                    .append("Year", l.getYear())
                    .append("Month", l.getMonthValue())
                    .append("Day", l.getDayOfMonth())
                    .append("Hour", l.getHour())
                    .append("Minute", l.getMinute())
                    .append("Second", l.getSecond());
        }
        if(document == null){
            throw new AtlasSerializationException(o);
        }
        return document;
    }
}
