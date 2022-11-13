package com.dndcraft.atlas.menu.icon;

import com.dndcraft.atlas.menu.MenuAgent;
import org.bukkit.inventory.ItemStack;

public abstract class Slot extends Icon {
	
	@Override
	public ItemStack getItemStack(MenuAgent agent) {
		int i = agent.getMenu().getIndexOf(this);
		return agent.getInventory().getItem(i);
	}
	
	public void setItemStack(MenuAgent agent, ItemStack is) {
		int i = agent.getMenu().getIndexOf(this);
		agent.getInventory().setItem(i, is);
	}
	
	@Override
	public boolean mayInteract(ItemStack moved) {
		return true;
	}
}
