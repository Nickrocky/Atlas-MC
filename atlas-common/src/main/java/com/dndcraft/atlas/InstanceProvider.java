package com.dndcraft.atlas;

import com.dndcraft.atlas.io.mongodb.AtlasStorage;
import com.dndcraft.atlas.io.mongodb.MongoConfiguration;
import com.dndcraft.atlas.io.sql.SQLHandler;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.File;
import java.io.IOException;

public class InstanceProvider {

    public static Atlas INSTANCE = null;
    public static SQLHandler SQL_HANDLER = null;
    public static MongoConfiguration MONGO_CONFIG = null;
    public static AtlasStorage MONGO_STORAGE = null;
    private static File AGNOSTICCONFIG = null;

    /**
     * This provides the global instance for Atlas, if you have made a new code module, you will need to call this method or all of your
     * Atlas-related code WILL NOT FUNCTION. To see a good example of non-spigot init() take a look in Atlas-Minestom.
     * */
    public static void init(Atlas atlas){
        if(INSTANCE != null) throw new IllegalStateException("Atlas could not be initialized as it was already initialized!");
        INSTANCE = atlas;
        if (!INSTANCE.getDataFolder().exists()) INSTANCE.getDataFolder().mkdir();
        initializeSQL();
        initializeAtlasAgnosticConfig();
        initializeMongoDB();
    }

    /**
     * Provides an SQL Database using SQLite to be shared by all plugins using Atlas
     * @Author Commissar_Voop, Nickrocky
     * */
    private static void initializeSQL(){
        if(SQL_HANDLER != null) throw new IllegalStateException("Atlas-SQLHandler could not be initialized as it was already initialized!");
        if(INSTANCE == null) throw new NullPointerException("SQL-Handler could not be initialized as Atlas hasn't! You likely called this method too early.");
        File sqlLiteFile = new File(INSTANCE.getDataFolder(), "AtlasDB.db");
        if(!sqlLiteFile.exists()){
            try{
                sqlLiteFile.createNewFile();
            }catch (IOException e){
                INSTANCE.getLogger().severe("Unable to create database file in " + INSTANCE.getDataFolder().getAbsolutePath() + " directory. Cancelling initialization!");
            }
        }
        SQL_HANDLER = new SQLHandler(sqlLiteFile);
    }

    /**
     * Creates a platform agnostic config file for the expressed purpose of storing MongoDB credentials.
     * This is required as credentials should NEVER be hard coded, and some minecraft platforms don't actually
     * have native support for config files. For example Minestom has no baked in YML support, unlike Bungee and Spigot
     * that typically do.
     * Besides NightConfig is better than any of minecrafts standard solutions
     * @ApiNote If you do not change <Replace Me> in config you will be thrown a Runtime exception so just be a smart cookie and change them
     * @Author Nickrocky
     * */
    private static void initializeAtlasAgnosticConfig(){
        AGNOSTICCONFIG = new File(INSTANCE.getDataFolder(), "AtlasConfig.toml");
        if(!AGNOSTICCONFIG.exists()){
            try {
                AGNOSTICCONFIG.createNewFile();
                saveDefaults(AGNOSTICCONFIG);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * Places slots that need to be replaced in the config file.
     * @ApiNote If you do not change <Replace Me> in config you will be thrown a Runtime exception so just be a smart cookie and change them
     * @Author Nickrocky
     * */
    private static void saveDefaults(File file){
        FileConfig config = FileConfig.of(file);
        config.load();
        config.set("Username", "<Replace Me>");
        config.set("Database", "<Replace Me>");
        config.set("Password", "<Replace Me>");
        config.set("IP_Address", "<Replace Me>");
        config.set("Port", 0);
        config.save();
        config.close();
    }

    /**
     * Creates a new instance of the AtlasStorage class that stores Atlas' ability to interact with MongoDB. Kinda important.
     * */
    private static void initializeMongoDB(){
        FileConfig config = FileConfig.of(AGNOSTICCONFIG);
        config.load();
        String username = config.get("Username");
        if(username.equalsIgnoreCase("<Replace Me>")) throw new RuntimeException("[Atlas] Error initializing agnostic config, values for MongoDB Database aren't set.");
        String database = config.get("Database");
        String password = config.get("Password");
        String ipaddress = config.get("IP_Address");
        String port = ""+config.get("Port");
        config.close();
        MONGO_CONFIG = new MongoConfiguration(username, database, password, ipaddress, port);
        MONGO_STORAGE = new AtlasStorage();
    }

}