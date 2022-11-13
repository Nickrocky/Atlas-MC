package com.dndcraft.atlas.data;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;


/**
 * All these do is tell the server how to convert these objects, in this case all of them are being turned from their
 * respective types into byte arrays which are natively serializable for the persistent data container
 * and then when we retrieve the object its just read from the byte array.
 * @Author Nickrocky213
 * @Date 12/27/2021
 * */
public interface AtlasDataTypes {
    PersistentDataType<byte[], ItemStack> ITEMSTACK = new AtlasDataType<>(ItemStack.class);
    PersistentDataType<byte[], Location> LOCATION = new AtlasDataType<>(Location.class);
    PersistentDataType<byte[], ItemStack[]> ITEMSTACK_ARRAY = new AtlasDataTypeArray<>(ItemStack.class, ItemStack[].class);
}
