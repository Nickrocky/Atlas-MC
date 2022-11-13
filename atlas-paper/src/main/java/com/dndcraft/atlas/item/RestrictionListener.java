package com.dndcraft.atlas.item;

import com.dndcraft.atlas.AtlasPaper;
import com.dndcraft.atlas.util.InventoryUtil;
import com.dndcraft.atlas.util.ItemUtil;
import com.dndcraft.atlas.util.Run;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RestrictionListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void inv(InventoryClickEvent e) {
		invInternal(e);
	}

	@EventHandler(ignoreCancelled = true)
	public void inv(InventoryDragEvent e) {
		invInternal(e);
	}
	
	private void invInternal(InventoryInteractEvent e) {
		if(e.getInventory().getType() == InventoryType.CRAFTING) {
			InventoryUtil.getResultOfEvent(e).stream()
			.filter(this::goesToArmor)
			.map(InventoryUtil.MovedItem::getItem)
			.filter(ItemRestriction.USE::isPresent)
			.findAny().ifPresent($->e.setCancelled(true));
		} else {
			InventoryUtil.getTouchedByEvent(e).stream()
			.filter(ItemRestriction.TRADE::isPresent)
			.findAny().ifPresent($->e.setCancelled(true));
		}
	}
	
	private boolean goesToArmor(InventoryUtil.MovedItem mi) {
		int finalSlot = mi.getFinalSlot();
		return finalSlot == 45 || (finalSlot > 4 && finalSlot < 9);
	}

	@EventHandler
	public void use(PlayerInteractEvent e) {
		ItemStack is = e.getItem();
		if(ItemUtil.exists(is) && ItemRestriction.USE.isPresent(is))
			e.setCancelled(true);
	}

	@EventHandler
	public void use(PlayerInteractEntityEvent e) {
		EquipmentSlot hand = e.getHand();
		PlayerInventory inv = e.getPlayer().getInventory();
		ItemStack is = hand == EquipmentSlot.HAND? inv.getItemInMainHand() : inv.getItemInOffHand();
		if(ItemUtil.exists(is) && ( ItemRestriction.USE.isPresent(is) || (ItemRestriction.TRADE.isPresent(is) && e.getRightClicked() instanceof ItemFrame) )) {
			e.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void dmg(EntityDamageByEntityEvent e) {
		Entity entity = e.getEntity();
		if(entity instanceof Player) {
			Player p = (Player) entity;
			ItemStack is = p.getInventory().getItemInMainHand();
			if(ItemUtil.exists(is) && ItemRestriction.USE.isPresent(is))
				e.setCancelled(true);
		}
	}


	@EventHandler(ignoreCancelled = true, priority=EventPriority.HIGHEST)
	public void handle(PlayerDropItemEvent e) {
		ItemStack is = e.getItemDrop().getItemStack();
		if(ItemRestriction.LOSE.isPresent(is)){
			e.setCancelled(true);
		} else if(ItemRestriction.TRADE.isPresent(is)) {
			Run.as(AtlasPaper.get()).sync(()->e.getItemDrop().remove());
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void craft(PrepareItemCraftEvent e) {
		CraftingInventory i = e.getInventory();
		Stream.of(i.getMatrix())
			  .filter(Objects::nonNull)
			  .filter(ItemRestriction.USE::isPresent)
			  .findAny().ifPresent($->i.setResult(null));
	}

	@EventHandler(ignoreCancelled = true)
	public void die(PlayerDeathEvent e) {
		if(e.getKeepInventory()) return; //Don't do anything if inventory is kept

		var items = e.getDrops().stream()
					 .filter(ItemRestriction.LOSE::isPresent)
					 .collect(Collectors.toList());

		items.forEach(e.getDrops()::remove);

		items = e.getDrops().stream()
				 .filter(ItemRestriction.TRADE::isPresent)
				 .collect(Collectors.toList());

		items.forEach(e.getDrops()::remove);
	}

	@EventHandler(ignoreCancelled = true)
	public void equip(PlayerSwapHandItemsEvent e) {
		if(ItemUtil.exists(e.getOffHandItem()) && ItemRestriction.USE.isPresent(e.getOffHandItem())) e.setCancelled(true);
	}
}