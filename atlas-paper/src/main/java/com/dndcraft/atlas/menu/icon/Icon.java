package com.dndcraft.atlas.menu.icon;

import com.dndcraft.atlas.menu.Menu;
import com.dndcraft.atlas.menu.MenuAction;
import com.dndcraft.atlas.menu.MenuAgent;
import org.bukkit.inventory.ItemStack;

public abstract class Icon {
	
	Icon(){}
	
	/**
	 * This gives the itemstack the Icon should be displaying
	 */
	public abstract ItemStack getItemStack(MenuAgent agent);
	
	/**
	 * Called when icon is interacted with by the player
	 */
	public abstract void click(MenuAction action);
	
	/**
	 * May you move an item in this icon (is the Icon a slot?)
	 * @param moved Item moved in (AIR if slot was emptied)
	 * @return whether or not you're allowed to move the item
	 */
	public boolean mayInteract(ItemStack moved) { return false; }
	
	/**
	 * Shortcut to let you update the displayed item within the menu quickly for this icon
	 */
	public void updateItem(Menu menu) {
		menu.updateIconItem(this);
	}
}
