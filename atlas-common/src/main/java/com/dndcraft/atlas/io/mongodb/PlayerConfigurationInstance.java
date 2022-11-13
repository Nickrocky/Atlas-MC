package com.dndcraft.atlas.io.mongodb;

import org.bson.Document;

import java.util.UUID;

public abstract class PlayerConfigurationInstance extends AtlasStorage {


    public static Document getConfigurationDocument(UUID uuid){
        Document document = new Document();
        document.append("Player_UUID", uuid.toString());
        return getPlayerConfiguration().find(document).first();
    }

    public abstract void registerPluginConfiguration(String plugin);

}
