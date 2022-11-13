package com.dndcraft.atlas;


import com.dndcraft.atlas.agnostic.AbstractComponentBuilder;
import com.dndcraft.atlas.io.mongodb.AtlasStorage;
import com.dndcraft.atlas.io.sql.SQLHandler;

import java.io.File;
import java.util.logging.Logger;

public interface Atlas {

    static Atlas get() {return InstanceProvider.INSTANCE;}

    static SQLHandler getSQLHandler(){ return InstanceProvider.SQL_HANDLER;}

    static AtlasStorage getMongoDBStorage(){ return InstanceProvider.MONGO_STORAGE; }

    File getDataFolder();

    Logger getLogger();

    AbstractComponentBuilder<?> componentBuilder();

}
