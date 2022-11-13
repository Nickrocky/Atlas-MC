package com.dndcraft.atlas.menu.icon;

import com.dndcraft.atlas.menu.MenuAction;
import com.dndcraft.atlas.menu.MenuAgent;
import org.bukkit.inventory.ItemStack;

public class SimpleSlot extends Slot {
	private ItemStack item;
	
	public SimpleSlot() {
		this(null);
	}
	
	public SimpleSlot(ItemStack item) {
		this.item = item;
	}

	@Override
	public void click(MenuAction action) {}
	
	@Override
	public ItemStack getItemStack(MenuAgent a) {
		return item;
	}

}
