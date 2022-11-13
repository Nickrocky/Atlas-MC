package com.dndcraft.atlas.util;

import com.dndcraft.atlas.data.AtlasDataTypes;
import lombok.Getter;
import org.bukkit.persistence.PersistentDataType;

public enum AtlasTypes {

    BYTE(PersistentDataType.BYTE),
    BYTE_ARRAY(PersistentDataType.BYTE_ARRAY),
    DOUBLE(PersistentDataType.DOUBLE),
    FLOAT(PersistentDataType.FLOAT),
    INTEGER(PersistentDataType.INTEGER),
    INTEGER_ARRAY(PersistentDataType.INTEGER_ARRAY),
    LONG(PersistentDataType.LONG),
    LONG_ARRAY(PersistentDataType.LONG_ARRAY),
    SHORT(PersistentDataType.SHORT),
    STRING(PersistentDataType.STRING),
    PERSISTENT_DATA_CONTAINER(PersistentDataType.TAG_CONTAINER),
    PERSISTENT_DATA_CONTAINER_ARRAY(PersistentDataType.TAG_CONTAINER_ARRAY),
    ITEMSTACK(AtlasDataTypes.ITEMSTACK),
    ITEMSTACK_ARRAY(AtlasDataTypes.ITEMSTACK_ARRAY),
    LOCATION(AtlasDataTypes.LOCATION),
    ;

    @Getter private final PersistentDataType dataType;

    AtlasTypes(PersistentDataType dataType){
        this.dataType = dataType;
    }

}
