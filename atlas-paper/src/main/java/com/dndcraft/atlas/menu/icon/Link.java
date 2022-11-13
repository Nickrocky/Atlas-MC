package com.dndcraft.atlas.menu.icon;

import com.dndcraft.atlas.menu.Menu;
import com.dndcraft.atlas.menu.MenuAction;
import com.dndcraft.atlas.menu.MenuAgent;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.inventory.ItemStack;

@FieldDefaults(makeFinal=true, level=AccessLevel.PRIVATE)
public class Link extends Button {
	Supplier<Menu> to;
	ItemStack item;
	
	public Link(ItemStack is, Menu menu) {
		to = Suppliers.ofInstance(menu);
		item = is;
	}
	
	public Link(ItemStack is, Supplier<Menu> supplier) {
		to = supplier;
		item = is;
	}
	
	public static Link memoize(ItemStack is, Supplier<Menu> supplier) {
		return new Link(is, Suppliers.memoize(supplier));
	}

	@Override
	public ItemStack getItemStack(MenuAgent agent) {
		return item;
	}

	@Override
	public void click(MenuAction action) {
		action.getMenuAgent().open(to.get());
	}

}
