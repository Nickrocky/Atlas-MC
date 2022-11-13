package com.dndcraft.atlas.convo;


import com.dndcraft.atlas.AtlasPaper;
import com.dndcraft.atlas.agnostic.AbstractChatStream;
import com.dndcraft.atlas.util.Run;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.bukkit.ChatColor.GRAY;

//Don't know if this will work
@RequiredArgsConstructor
class PromptListener<T extends Event> implements Consumer<AbstractChatStream.Prompt>,Listener,EventExecutor {
	final UUID uuid;
	final Class<T> clazz;
	final Function<T, Object> function;
	final Function<T, Player> howToGetPlayer;
	
	AbstractChatStream.Prompt prompt;
	BukkitTask giveUp;
	
	@Override
	public void accept(AbstractChatStream.Prompt p) {
		prompt = p;
		task();
		var atlasPaper = AtlasPaper.get();
		var m = Bukkit.getPluginManager();
		
		m.registerEvent(AsyncPlayerChatEvent.class, this, EventPriority.LOW, this, atlasPaper, true);
		m.registerEvent(InventoryOpenEvent.class, this, EventPriority.LOW, this, atlasPaper, true);
		if(clazz != AsyncPlayerChatEvent.class && clazz != InventoryOpenEvent.class)
			m.registerEvent(clazz, this, EventPriority.LOW, this, atlasPaper, true);
	}

	@Override
	public void execute(Listener listener, Event event) throws EventException {
		//CoreLog.debug("Catching Event as " + this);
		
		if(event instanceof InventoryOpenEvent) { //Cant open inventories while in coversation
			InventoryOpenEvent ioe = (InventoryOpenEvent) event;
			if(ioe.getPlayer().getUniqueId().equals(uuid)) ioe.setCancelled(true);
		}else if(event instanceof AsyncPlayerChatEvent) {
			var as = (AsyncPlayerChatEvent) event;
			if(as.getPlayer().getUniqueId().equals(uuid)) {
				as.setCancelled(true);
				if("stop".equals(as.getMessage().toLowerCase())) {
					as.getPlayer().sendMessage(Component.text(GRAY + "Stop command received. Exiting"));
					Run.as(AtlasPaper.get()).sync(()->abandon());
					return;
				}
			}
		}
		
		if(clazz.isInstance(event)) {
			T theEvent = clazz.cast(event);
			Player who = howToGetPlayer.apply(theEvent);
			if(who != null && who.getUniqueId().equals(uuid)) {
				task();
				Object result = function.apply(theEvent);
				if(result != null) {
					close();
					prompt.fulfil(result);
					//CoreLog.debug("Prompt was fulfilled: " + this);
				} else {
					prompt.sendPrompt();
				}
			}
		}
	}
	
	private void task() {
		if(giveUp != null) giveUp.cancel();
		giveUp = Bukkit.getScheduler().runTaskLater(AtlasPaper.get(), ()->{
			giveUp = null;
			abandon();
			Player p = Bukkit.getPlayer(uuid);
			if(p != null) p.sendMessage(GRAY + "We didn't receive your input in time. Exiting.");
			//CoreLog.debug("Giving up on prompt " + this);
		}, 40*20);
	}
	
	private void close() {
		HandlerList.unregisterAll(this);
		if(giveUp != null) giveUp.cancel();
	}
	
	private void abandon() {
		close();
		prompt.abandon();
	}
	
	@Override
	public String toString() {
		return "HeyListen{" + uuid.toString() + "->" + LegacyComponentSerializer.legacyAmpersand().serialize(prompt.getText()) + "}";
	}
}
