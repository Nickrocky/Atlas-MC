package com.dndcraft.atlas.util;

import com.dndcraft.atlas.data.AtlasDataTypes;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class EntityUtil extends PersistentDataUtil{

    /**
     * Adds an NBT tag storing a type to an entity which is referencable by a key
     * @param entity entity you want to add a tag to
     * @param key reference key for the stored data
     * @param value value you would like to store
     * */
    public static void setTag(Entity entity, String key, byte value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.BYTE, value);
    }

    public static void setTag(Entity entity, String key, byte[] value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.BYTE_ARRAY, value);
    }

    public static void setTag(Entity entity, String key, double value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.DOUBLE, value);
    }

    public static void setTag(Entity entity, String key, float value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.FLOAT, value);
    }

    public static void setTag(Entity entity, String key, int value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.INTEGER, value);
    }

    public static void setTag(Entity entity, String key, int[] value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.INTEGER_ARRAY, value);
    }

    public static void setTag(Entity entity, String key, long value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.LONG, value);
    }

    public static void setTag(Entity entity, String key, long[] value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.LONG_ARRAY, value);
    }

    public static void setTag(Entity entity, String key, short value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.SHORT, value);
    }

    public static void setTag(Entity entity, String key, String value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.STRING, value);
    }

    public static void setTag(Entity entity, String key, PersistentDataContainer value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.TAG_CONTAINER, value);
    }

    public static void setTag(Entity entity, String key, PersistentDataContainer[] value){
        setTag(entity.getPersistentDataContainer(), key, PersistentDataType.TAG_CONTAINER_ARRAY, value);
    }

    public static void setTag(Entity entity, String key, ItemStack value){
        setTag(entity.getPersistentDataContainer(), key, AtlasDataTypes.ITEMSTACK, value);
    }

    public static void setTag(Entity entity, String key, ItemStack[] value){
        setTag(entity.getPersistentDataContainer(), key, AtlasDataTypes.ITEMSTACK_ARRAY, value);
    }

    public static void setTag(Entity entity, String key, Location value){
        setTag(entity.getPersistentDataContainer(), key, AtlasDataTypes.LOCATION, value);
    }

    public static <T extends PersistentDataContainer> byte getByteTag(Entity entity, String key){
        return (byte) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.BYTE);
    }

    public static <T extends PersistentDataContainer> byte[] getByteArrTag(Entity entity, String key){
        return (byte[]) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.BYTE_ARRAY);
    }

    public static <T extends PersistentDataContainer> double getDoubleTag(Entity entity, String key){
        return (double) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.DOUBLE);
    }

    public static <T extends PersistentDataContainer> float getFloatTag(Entity entity, String key){
        return (float) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.FLOAT);
    }

    public static <T extends PersistentDataContainer> int getIntTag(Entity entity, String key){
        return (int) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.INTEGER);
    }

    public static <T extends PersistentDataContainer> int[] getIntArrTag(Entity entity, String key){
        return (int[]) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.INTEGER_ARRAY);
    }

    public static <T extends PersistentDataContainer> long getLongTag(Entity entity, String key){
        return (long) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.LONG);
    }

    public static <T extends PersistentDataContainer> long[] getLongArrTag(Entity entity, String key){
        return (long[]) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.LONG_ARRAY);
    }

    public static <T extends PersistentDataContainer> short getCharTag(Entity entity, String key){
        return (short) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.SHORT);
    }

    public static <T extends PersistentDataContainer> String getStringTag(Entity entity, String key){
        return (String) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.STRING);
    }

    public static <T extends PersistentDataContainer> PersistentDataContainer getPersistentDataContainerTag(Entity entity, String key){
        return (PersistentDataContainer) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.TAG_CONTAINER);
    }

    public static <T extends PersistentDataContainer> PersistentDataContainer[] getPersistentDataContainerArrTag(Entity entity, String key){
        return (PersistentDataContainer[]) getTag((T) entity.getPersistentDataContainer(), key, PersistentDataType.TAG_CONTAINER_ARRAY);
    }

    public static <T extends PersistentDataContainer> Location getLocationTag(Entity entity, String key){
        return (Location) getTag((T) entity.getPersistentDataContainer(), key, AtlasDataTypes.LOCATION);
    }

    public static <T extends PersistentDataContainer> ItemStack getItemStackTag(Entity entity, String key){
        return (ItemStack) getTag((T) entity.getPersistentDataContainer(), key, AtlasDataTypes.ITEMSTACK);
    }

    public static <T extends PersistentDataContainer> ItemStack[] getItemStackArrTag(Entity entity, String key){
        return (ItemStack[]) getTag((T) entity.getPersistentDataContainer(), key, AtlasDataTypes.ITEMSTACK_ARRAY);
    }

    public static <T extends PersistentDataContainer> boolean hasTag(Entity entity, String key){
        for(AtlasTypes dataTypes : AtlasTypes.values()){
            if(hasTag((T)entity.getPersistentDataContainer(), key, dataTypes.getDataType())){
                return true;
            }
        }
        return false;
    }


}
