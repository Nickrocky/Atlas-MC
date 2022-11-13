package com.dndcraft.atlas.menu.icon;

import org.bukkit.inventory.ItemStack;

public abstract class Button extends Icon {

	@Override
	public final boolean mayInteract(ItemStack moved) {
		return false;
	}
}
