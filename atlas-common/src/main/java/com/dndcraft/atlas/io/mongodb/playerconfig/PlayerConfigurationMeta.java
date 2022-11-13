package com.dndcraft.atlas.io.mongodb.playerconfig;

import com.dndcraft.atlas.util.MongoConfigType;
import com.dndcraft.atlas.io.mongodb.interfaces.ISerializable;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

@Getter
@Setter
public class PlayerConfigurationMeta implements ISerializable {

    private String registeringPlugin;
    private MongoConfigType type;

    public PlayerConfigurationMeta(String registeringPlugin){
        this.registeringPlugin = registeringPlugin;
        this.type = MongoConfigType.COMPLEX;
    }

    public PlayerConfigurationMeta(){}

    @Override
    public Document toDocument() {
        Document document = new Document();
        document.append("Registering_Plugin", registeringPlugin);
        document.append("Config_Type", type.name());
        return document;
    }

    @Override
    public void fromDocument(Document document) {
        this.registeringPlugin = document.getString("Registering_Plugin");
        this.type = MongoConfigType.valueOf(document.getString("Config_Type"));
    }
}
