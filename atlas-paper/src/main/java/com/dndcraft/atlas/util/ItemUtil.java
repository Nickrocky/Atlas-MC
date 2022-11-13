package com.dndcraft.atlas.util;

import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.comphenix.protocol.wrappers.nbt.NbtList;
import com.dndcraft.atlas.data.AtlasDataTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemUtil extends PersistentDataUtil{


    public static ItemStack make(Material baseMaterial, Component displayName){
        return make(baseMaterial, 1, displayName, new ArrayList<>());
    }

    public static ItemStack make(Material baseMaterial, Component displayName, Component... lore){
        return make(baseMaterial, 1, displayName, Arrays.stream(lore).toList());
    }

    public static ItemStack make(Material baseMaterial, Component displayName, List<Component> lore){
        return make(baseMaterial, 1, displayName, lore);
    }

    public static ItemStack make(Material baseMaterial, int amount, Component displayName){
        return make(baseMaterial, amount, displayName, new ArrayList<>());
    }

    public static ItemStack make(Material baseMaterial, int amount, Component displayName, Component... lore){
        return make(baseMaterial, amount, displayName, Arrays.stream(lore).toList());
    }

    public static ItemStack make(Material baseMaterial, int amount, Component displayName, List<Component> lore){
        ItemStack i = new ItemStack(baseMaterial, amount);
        return decorate(i, displayName, lore);
    }

    public static ItemStack makePotion(Color potionColor, Component displayName, List<Component> lore){
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta pMeta = (PotionMeta) potion.getItemMeta();
        pMeta.setColor(potionColor);
        return decorate(potion, displayName, lore);
    }

    public static ItemStack decorate(ItemStack item, Component displayName, List<Component> lore){
        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Convenience method for checking if an ItemStack is non-null and non-AIR
     * @param is ItemStack to check
     * @return is != null && is.getType() != Material.AIR;
     */
    @SuppressWarnings("deprecation")
    public static boolean exists(ItemStack is) {
        return is != null
                && is.getType() != Material.AIR
                && is.getType() != Material.CAVE_AIR
                && is.getType() != Material.VOID_AIR
                && is.getType() != Material.LEGACY_AIR;
    }

    /**
     * NBT Tag stuff below this point, there are a ton of methods here, though its done like this to allow for minimal effort
     * on another devs part in using the setTag function
     * */


    //For better backwards compat
    /**
     * Atlas used to only support Strings for an NBT Type not because it would be significantly hard to program
     * but because most things can be just yeeted into a string and converted back accordingly. However,
     * with the induction of things like Markers and other persistent data containers being added its probably time
     * to support more than just strings, these methods below may not share the old name of the method, but it will a
     * quick fix. I'd rather everyone learn the new way the API is laid out than the yike old way.
     *
     * I chose to make setTag take an ItemStack instead of ItemMeta due to the ease of it, in the old system there
     * was a helper method that just quickly did ItemStack#getItemMeta which worked, but now that we have every
     * persistent data type and more, it would just look like code bloat. If its a problem later I guess its a
     * copy-paste away.
     * */

    /**
     * Sets a tag to
     * */
    public static void setTag(ItemMeta meta, String key, String value){
        setTag(meta.getPersistentDataContainer(), key, PersistentDataType.STRING, value);
    }

    public static <T extends PersistentDataContainer> boolean hasTag(ItemMeta meta, String key){
        for(AtlasTypes dataTypes : AtlasTypes.values()){
            if(hasTag((T)meta.getPersistentDataContainer(), key, dataTypes.getDataType())){
                return true;
            }
        }
        return false;
    }

    public static <T extends PersistentDataContainer> String getStringTag(ItemMeta meta, String key){
        return (String) getTag((T) meta.getPersistentDataContainer(), key, PersistentDataType.STRING);
    }

    /**
     * For private use in this file, this is only due to the nature of the Persistent data container being located inside of the ItemStack's ItemMeta
     * */
    private static void setAtlasTag(ItemStack itemStack, String key, AtlasTypes type, Object value){
        ItemMeta meta = itemStack.getItemMeta();
        switch (type){
            case BYTE -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.BYTE, value);
            case BYTE_ARRAY -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.BYTE_ARRAY, value);
            case DOUBLE -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.DOUBLE, value);
            case LONG -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.LONG, value);
            case FLOAT -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.FLOAT, value);
            case SHORT -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.SHORT, value);
            case STRING -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.STRING, value);
            case INTEGER -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.INTEGER, value);
            case LOCATION -> setTag(meta.getPersistentDataContainer(), key, AtlasTypes.LOCATION.getDataType(), value);
            case ITEMSTACK -> setTag(meta.getPersistentDataContainer(), key, AtlasTypes.ITEMSTACK.getDataType(), value);
            case ITEMSTACK_ARRAY -> setTag(meta.getPersistentDataContainer(), key, AtlasTypes.ITEMSTACK_ARRAY.getDataType(), value);
            case LONG_ARRAY -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.LONG_ARRAY, value);
            case INTEGER_ARRAY -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.INTEGER_ARRAY, value);
            case PERSISTENT_DATA_CONTAINER -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.TAG_CONTAINER, value);
            case PERSISTENT_DATA_CONTAINER_ARRAY -> setTag(meta.getPersistentDataContainer(), key, PersistentDataType.TAG_CONTAINER_ARRAY, value);
        }
        itemStack.setItemMeta(meta);
    }

    /**
     * Adds an NBT tag storing a type to an itemstack which is referencable by a key
     * @param itemStack item you want to add a tag to
     * @param key reference key for the stored data
     * @param value value you would like to store
     * */
    public static void setTag(ItemStack itemStack, String key, byte value){
        setAtlasTag(itemStack, key, AtlasTypes.BYTE, value);
    }

    public static void setTag(ItemStack itemStack, String key, byte[] value){
        setAtlasTag(itemStack, key, AtlasTypes.BYTE_ARRAY, value);
    }

    public static void setTag(ItemStack itemStack, String key, double value){
        setAtlasTag(itemStack, key, AtlasTypes.DOUBLE, value);
    }

    public static void setTag(ItemStack itemStack, String key, float value){
        setAtlasTag(itemStack, key, AtlasTypes.FLOAT, value);
    }

    public static void setTag(ItemStack itemStack, String key, int value){
        setAtlasTag(itemStack, key, AtlasTypes.INTEGER, value);
    }

    public static void setTag(ItemStack itemStack, String key, int[] value){
        setAtlasTag(itemStack, key, AtlasTypes.INTEGER_ARRAY, value);
    }

    public static void setTag(ItemStack itemStack, String key, long value){
        setAtlasTag(itemStack, key, AtlasTypes.LONG, value);
    }

    public static void setTag(ItemStack itemStack, String key, long[] value){
        setAtlasTag(itemStack, key, AtlasTypes.LONG_ARRAY, value);
    }

    public static void setTag(ItemStack itemStack, String key, short value){
        setAtlasTag(itemStack, key, AtlasTypes.SHORT, value);
    }

    public static void setTag(ItemStack itemStack, String key, String value){
        setAtlasTag(itemStack, key, AtlasTypes.STRING, value);
    }

    public static void setTag(ItemStack itemStack, String key, PersistentDataContainer value){
        setAtlasTag(itemStack, key, AtlasTypes.PERSISTENT_DATA_CONTAINER, value);
    }

    public static void setTag(ItemStack itemStack, String key, PersistentDataContainer[] value){
        setAtlasTag(itemStack, key, AtlasTypes.PERSISTENT_DATA_CONTAINER_ARRAY, value);
    }

    public static void setTag(ItemStack itemStack, String key, ItemStack value){
        setAtlasTag(itemStack, key, AtlasTypes.ITEMSTACK, value);
    }

    public static void setTag(ItemStack itemStack, String key, ItemStack[] value){
        setAtlasTag(itemStack, key, AtlasTypes.ITEMSTACK_ARRAY, value);
    }

    public static void setTag(ItemStack itemStack, String key, Location value){
        setAtlasTag(itemStack, key, AtlasTypes.LOCATION, value);
    }

    public static <T extends PersistentDataContainer> byte getByteTag(ItemStack itemStack, String key){
        return (byte) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.BYTE);
    }

    public static <T extends PersistentDataContainer> byte[] getByteArrTag(ItemStack itemStack, String key){
        return (byte[]) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.BYTE_ARRAY);
    }

    public static <T extends PersistentDataContainer> double getDoubleTag(ItemStack itemStack, String key){
        return (double) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.DOUBLE);
    }

    public static <T extends PersistentDataContainer> float getFloatTag(ItemStack itemStack, String key){
        return (float) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.FLOAT);
    }

    public static <T extends PersistentDataContainer> int getIntTag(ItemStack itemStack, String key){
        return (int) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.INTEGER);
    }

    public static <T extends PersistentDataContainer> int[] getIntArrTag(ItemStack itemStack, String key){
        return (int[]) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.INTEGER_ARRAY);
    }

    public static <T extends PersistentDataContainer> long getLongTag(ItemStack itemStack, String key){
        return (long) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.LONG);
    }

    public static <T extends PersistentDataContainer> long[] getLongArrTag(ItemStack itemStack, String key){
        return (long[]) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.LONG_ARRAY);
    }

    public static <T extends PersistentDataContainer> short getCharTag(ItemStack itemStack, String key){
        return (short) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.SHORT);
    }

    public static <T extends PersistentDataContainer> String getStringTag(ItemStack itemStack, String key){
        return (String) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.STRING);
    }

    public static <T extends PersistentDataContainer> PersistentDataContainer getPersistentDataContainerTag(ItemStack itemStack, String key){
        return (PersistentDataContainer) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.TAG_CONTAINER);
    }

    public static <T extends PersistentDataContainer> PersistentDataContainer[] getPersistentDataContainerArrTag(ItemStack itemStack, String key){
        return (PersistentDataContainer[]) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, PersistentDataType.TAG_CONTAINER_ARRAY);
    }

    public static <T extends PersistentDataContainer> Location getLocationTag(ItemStack itemStack, String key){
        return (Location) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, AtlasDataTypes.LOCATION);
    }

    public static <T extends PersistentDataContainer> ItemStack getItemStackTag(ItemStack itemStack, String key){
        return (ItemStack) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, AtlasDataTypes.ITEMSTACK);
    }

    public static <T extends PersistentDataContainer> ItemStack[] getItemStackArrTag(ItemStack itemStack, String key){
        return (ItemStack[]) getTag((T) itemStack.getItemMeta().getPersistentDataContainer(), key, AtlasDataTypes.ITEMSTACK_ARRAY);
    }

    public static <T extends PersistentDataContainer> boolean hasTag(ItemStack itemStack, String key){
        if(!exists(itemStack)) return false;
        for(AtlasTypes dataTypes : AtlasTypes.values()){
            if(hasTag((T)itemStack.getItemMeta().getPersistentDataContainer(), key, dataTypes.getDataType())){
                return true;
            }
        }
        return false;
    }

    public static <T extends PersistentDataContainer> void removeTag(ItemStack itemStack, String key){
        removeTag((T) itemStack, key);
    }

    /**
     * Method to easily make Minecraft skulls from arbitrary skin files
     * @param theTexture A base-64 encoded nbt compound containing skin info
     * @return a textured Minecraft SKULL_ITEM
     */
    public static ItemStack getSkullFromTexture(String theTexture) {
        NbtCompound tag = NbtFactory.ofCompound("tag");
        NbtCompound skullOwner = NbtFactory.ofCompound("SkullOwner");
        NbtCompound properties = NbtFactory.ofCompound("Properties");
        NbtCompound property = NbtFactory.ofCompound("");

        char[] uid = UUID.nameUUIDFromBytes(theTexture.getBytes()).toString().toCharArray();
        uid[14] = '4';
        uid[19] = 'a';

        property.put("Value", theTexture);
        skullOwner.put("Id", String.valueOf(uid));
        NbtList<NbtCompound> list = NbtFactory.ofList("textures", property);
        properties.put(list);
        skullOwner.put(properties);
        tag.put(skullOwner);

        ItemStack is = MinecraftReflection.getBukkitItemStack(new ItemStack(Material.PLAYER_HEAD, 1));
        NbtFactory.setItemTag(is, tag);
        return is;
    }

    /**
     * Method to easily make Minecraft skulls from arbitrary skin files
     * @param profile The profile to get a skin from
     * @return a textured Minecraft SKULL_ITEM
     */
    public static ItemStack getSkullFromProfile(WrappedGameProfile profile) {
        String value = profile.getProperties().get("textures").iterator().next().getValue();
        return getSkullFromTexture(value);
    }

}
