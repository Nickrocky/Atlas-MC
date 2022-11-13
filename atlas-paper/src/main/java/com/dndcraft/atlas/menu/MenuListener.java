package com.dndcraft.atlas.menu;

import com.dndcraft.atlas.AtlasPaper;
import com.dndcraft.atlas.menu.icon.Icon;
import com.dndcraft.atlas.util.InventoryUtil;
import com.dndcraft.atlas.util.ItemUtil;
import com.dndcraft.atlas.util.Run;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuListener implements Listener {

	//Two possible interactions
	//a) SINGLE icon/slot affected, propagate a click
	//b) MULTIPLE slots affected; they all allowed a move

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void handle(InventoryCloseEvent e) {
		MenuAgent agent = getAgent(e.getInventory(), e.getPlayer());
		if(agent != null) {
			//CoreLog.debug("Closing Menu for player: " + e.getPlayer().getName());
			agent.getMenu().clearViewer(agent);
			if(!agent.isNavigating()) {
				//Session ended. Player either closed inv manually or MenuAgent::close was called.
				agent.runSessionClosureCallbacks();
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void handle(InventoryOpenEvent e) {
		var inv = e.getInventory();
		if(inv.getHolder() instanceof Menu) {
			Menu menu = (Menu) inv.getHolder();
			menu.init();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void inv(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		Inventory top = e.getView().getTopInventory();
		Inventory clicked = e.getClickedInventory();
		
		if (clicked != null && (clicked.getHolder() instanceof Menu || (top != clicked && top.getHolder() instanceof Menu))) {
			MenuAgent agent = getAgent(top, e.getWhoClicked());
			if (agent != null) {
				MenuAction action = new MenuAction(agent, InventoryUtil.getResultOfEvent(e), e.getClick());
				List<Icon> queued = new ArrayList<>();

				boolean cancel = setCancel(e, action);
				
				if (cancel && (top == clicked)) { //clicking about a menu
					if (action.getMovedItems().size() == 1 && ItemUtil.exists(e.getView().getItem(e.getRawSlot()))) {
						asIcon(e, e.getRawSlot(), action).ifPresent(queued::add);
					}
				} else if (!cancel) { // moving items in or out of a menu
					for (InventoryUtil.MovedItem mi : action.getMovedItems()) {
						//Should be impossible for both to be true
						asIcon(e, mi.getInitialSlot(), action).ifPresent(queued::add);
						asIcon(e, mi.getFinalSlot(), action).ifPresent(queued::add);
					}
				} //else should never be called ?
				maybeRun(queued, action);
				e.setCancelled(cancel);
				return;
			}
			e.setCancelled(true);
		} 
	}

	private void maybeRun(List<Icon> clicked, MenuAction action) {
		if(!clicked.isEmpty()) Run.as(AtlasPaper.get()).sync( ()-> {
			Player p = action.getPlayer();
			if(!p.isOnline())
				return;
			if(p.getOpenInventory() == null)
				return;
			if(!(p.getOpenInventory().getTopInventory().getHolder() instanceof Menu) )
				return;
			clicked.forEach(i->i.click(action));
		});
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void inv(InventoryDragEvent e) {
		if (e.getInventory().getHolder() instanceof Menu) {
			MenuAgent agent = getAgent(e.getInventory(), e.getWhoClicked());
			if (agent != null) {
				ArrayList<MenuAction> actionList = new ArrayList<>();
				actionList.add(new MenuAction(agent, InventoryUtil.getResultOfEvent(e), ClickType.LEFT));
				actionList.add(new MenuAction(agent, InventoryUtil.getResultOfEvent(e), ClickType.SHIFT_LEFT));
				actionList.add(new MenuAction(agent, InventoryUtil.getResultOfEvent(e), ClickType.SHIFT_RIGHT));

				for (MenuAction action : actionList) {
					if (!setCancel(e, action)) {
						List<Icon> queued = new ArrayList<>();

						for (Integer slot : e.getRawSlots()) {
							asIcon(e, slot, action).ifPresent(queued::add);
						}

						maybeRun(queued, action);
					}
				}
			}
			e.setCancelled(true);
		}
	}

	private Optional<Icon> asIcon(InventoryInteractEvent e, int slot, MenuAction a){
		if(slot < 0 || slot >= e.getInventory().getSize()) return Optional.empty();
		return Optional.ofNullable(a.getMenuAgent().getMenu().getIcon(slot));
	}

	//Returns if cancellation was invoked
	//If so, treat as click and enforce single-item moved
	private boolean setCancel(InventoryInteractEvent e, MenuAction a) {
		List<InventoryUtil.MovedItem> moved = a.getMovedItems();

		//Check if any immovable icons are being moved
		//if so cancel the event
		for(InventoryUtil.MovedItem mi : moved) {
			var item = mi.getItem();
			if(asIcon(e, mi.getInitialSlot(), a).filter(i->!i.mayInteract(new ItemStack(Material.AIR))).isPresent()) {
				//e.setCancelled(true);
				return true;
			}

			if(asIcon(e, mi.getFinalSlot(), a).filter(i->!i.mayInteract(item)).isPresent()) {
				//e.setCancelled(true);
				return true;
			}
		}
		return false;
	}

	private MenuAgent getAgent(Inventory inv, HumanEntity he) {
		var holder = inv.getHolder();
		if(holder instanceof Menu) {
			Menu menu = (Menu) holder;
			MenuAgent agent = menu.getAgent(he);
			Validate.notNull(agent);
			return agent;
		} else {
			return null;
		}
	}

}
