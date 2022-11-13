package com.dndcraft.atlas.util;

import com.dndcraft.atlas.agnostic.AbstractComponentBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class BukkitComponentBuilder extends AbstractComponentBuilder<BukkitComponentBuilder> {

	public BukkitComponentBuilder() {
		super("");
	}
	
	public BukkitComponentBuilder(String initial) {
		super(initial);
	}
	
	public BukkitComponentBuilder hoverShowItem(ItemStack is) {
		if(is.getItemMeta().hasDisplayName()){
			handle.append(is.getItemMeta().displayName().hoverEvent(is));
		}else{
			handle.append(Component.text(is.getI18NDisplayName()).hoverEvent(is));
		}
		return getThis();
	}
	
	@Override
	protected BukkitComponentBuilder getThis() {
		return this;
	}
	
	public BukkitComponentBuilder send(CommandSender s) {
		s.sendMessage(handle.build());
		return getThis();
	}

}
