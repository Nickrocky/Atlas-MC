package com.dndcraft.atlas.menu.icon;

import com.dndcraft.atlas.util.ItemUtil;
import com.dndcraft.atlas.menu.MenuAction;
import com.dndcraft.atlas.menu.MenuAgent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class Pad extends Icon {
	@Getter private final ItemStack itemStack;

	public Pad(Material m) {
		itemStack = ItemUtil.make(m, Component.text("", NamedTextColor.GRAY));
	}
		
	@Override
	public void click(MenuAction action) {}

	@Override
	public ItemStack getItemStack(MenuAgent agent) {
		return getItemStack();
	}

}
