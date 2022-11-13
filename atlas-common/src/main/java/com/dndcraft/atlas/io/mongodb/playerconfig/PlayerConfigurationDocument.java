package com.dndcraft.atlas.io.mongodb.playerconfig;

import com.dndcraft.atlas.util.MongoConfigType;
import com.dndcraft.atlas.io.mongodb.interfaces.ISerializable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerConfigurationDocument implements ISerializable {

    private UUID playerUUID;
    private List<PlayerConfigurationMeta> metaList;
    private HashMap<String, Document> playerConfigurationDocuments;

    public void createConfiguration(UUID uuid){

    }

    @Override
    public Document toDocument() {
        Document document = new Document();
        document.append("Player_UUID", playerUUID.toString());
        List<Document> metaDocuments = new ArrayList<>();
        for(PlayerConfigurationMeta playerConfigurationMeta : metaList){
            metaDocuments.add(playerConfigurationMeta.toDocument());
        }
        document.append("Meta_Documents", metaDocuments);
        return document;
    }

    @Override
    public void fromDocument(Document document) {
        this.playerUUID = UUID.fromString(document.getString("Player_UUID"));
        for(Document metaDocument : (List<Document>) document.get("Meta_Documents")){
            PlayerConfigurationMeta meta = new PlayerConfigurationMeta();
            if(meta.getType()== MongoConfigType.SIMPLE){
                SimplePluginPlayerConfig playerConfig = new SimplePluginPlayerConfig(meta.getRegisteringPlugin());
                playerConfig.fromDocument(metaDocument);
                metaList.add(playerConfig);
            }else{
                meta.fromDocument(metaDocument);
                metaList.add(meta);
            }
            playerConfigurationDocuments.put(meta.getRegisteringPlugin(), metaDocument);
        }
    }





    /**
     * Gets the document associated with a particular config
     * @ApiNote: YOU WILL HAVE TO PROCESS THIS TO GET YOUR CUSTOM CONFIG OUT
     * */
    public Document getInstance(String registeringPlugin){
        if(hasInstance(registeringPlugin)){
            return playerConfigurationDocuments.get(registeringPlugin);
        }
        return null;
    }

    /**
     * Checks if there is an instance of this particular configuration.
     * @Apinote this will return false if a particular player has never been given a meta for your plugins config.
     * @param registeringPlugin the plugin that registered the config
     * @return true if there is a config stored for a particular plugin on a player
     * */
    public boolean hasInstance(String registeringPlugin){
        for(PlayerConfigurationMeta meta : metaList){
            if(meta.getRegisteringPlugin().equalsIgnoreCase(registeringPlugin)) return true;
        }
        return false;
    }

}
