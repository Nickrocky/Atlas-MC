package com.dndcraft.atlas.io.mongodb.playerconfig;

import com.dndcraft.atlas.io.mongodb.interfaces.ISerializable;
import org.bson.Document;

import java.util.HashMap;

/**
 * @Author Nickrocky213
 * @Date: 6/2/2021
 * NOTE YOU CAN ONLY SAVE JAVA PRIMITIVE TYPES USING THE SIMPLE PLUGIN PLAYER CONFIG
 * IF YOU NEED SOMETHING WITH MORE FLEXIBILITY OR JUST HATE THIS CLASS USE {@link PlayerConfigurationDocument}
 * */
public class SimplePluginPlayerConfig extends PlayerConfigurationMeta implements ISerializable {

    public HashMap<String, Object> storedValues = new HashMap<>();

    public SimplePluginPlayerConfig(String registeredPlugin){
        super(registeredPlugin);
    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        document.append("Registering_Plugin", getRegisteringPlugin());
        document.append("Configuration_Map", storedValues);
        return document;
    }

    @Override
    public void fromDocument(Document document) {
        setRegisteringPlugin(document.getString("Registering_Plugin"));
        storedValues = (HashMap<String, Object>) document.get("Configuration_Map");
    }
}
