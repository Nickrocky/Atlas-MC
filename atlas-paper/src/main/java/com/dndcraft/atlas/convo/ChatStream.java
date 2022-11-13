package com.dndcraft.atlas.convo;

import com.dndcraft.atlas.AtlasPaper;
import com.dndcraft.atlas.agnostic.AbstractChatStream;
import com.dndcraft.atlas.wrapper.BukkitSender;
import com.dndcraft.atlas.util.Run;
import com.google.common.base.Predicates;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.function.Function;
import java.util.function.Predicate;

public class ChatStream extends AbstractChatStream<ChatStream> {
	
	public ChatStream(Player p) {
		super(new BukkitSender(p), p.getUniqueId());
	}

	public <T extends PlayerEvent> ChatStream listen(String contextTag, Component message, Class<T> c, Function<T, Object> listener) {
		return listen(contextTag, message, c, listener, e->e.getPlayer());
	}
	
	public <T extends Event> ChatStream listen(String contextTag, Component message, Class<T> c, Function<T, Object> listener, Function<T, Player> howToGetPlayer) {
		prompt(contextTag, message, new PromptListener<>(uuid, c, listener, howToGetPlayer));
		return this;
	}
	
	@Override
	public ChatStream prompt(String contextTag, Component message, Predicate<String> filter, Function<String, ?> mapper) {
		return listen(contextTag, message, AsyncPlayerChatEvent.class, x->{
			if(filter.test(x.getMessage())) return mapper.apply(x.getMessage());
			else return null;
		});
	}
	
	public ChatStream clickBlockPrompt(String contextTag, String message) {
		return clickBlockPrompt(contextTag, Component.text(message));
	}
	
	public ChatStream clickBlockPrompt(String contextTag, String message, Predicate<Block> filter) {
		return clickBlockPrompt(contextTag, Component.text(message), filter);
	}
	
	public ChatStream clickBlockPrompt(String contextTag, Component message) {
		return clickBlockPrompt(contextTag, message, Predicates.alwaysTrue());
	}
	
	public ChatStream clickBlockPrompt(String contextTag, Component message, Predicate<Block> filter) {
		return listen(contextTag, message, PlayerInteractEvent.class, e->{
				if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					Block b = e.getClickedBlock();
					e.setCancelled(true);
					if(filter.test(b)) return b;
				}
				return null;
		});
	}
	
	public ChatStream clickEntityPrompt(String contextTag, String message) {
		return clickEntityPrompt(contextTag, Component.text(message));
	}
	
	public ChatStream clickEntityPrompt(String contextTag, String message, Predicate<Entity> filter) {
		return clickEntityPrompt(contextTag, Component.text(message), filter);
	}
	
	public ChatStream clickEntityPrompt(String contextTag, Component message) {
		return clickEntityPrompt(contextTag, message, Predicates.alwaysTrue());
	}
	
	public ChatStream clickEntityPrompt(String contextTag, Component message, Predicate<Entity> filter) {
		return listen(contextTag, message, PlayerInteractEntityEvent.class, e->{
				Entity entity = e.getRightClicked();
				e.setCancelled(true);
				if(filter.test(entity)) return entity;
				return null;
		});
	}
	
	@Override
	protected ChatStream getThis() {
		return this;
	}

	@Override
	protected void resolveFinishedStream() {
		if(Bukkit.isPrimaryThread()) onActivate.accept(context);
		else Run.as(AtlasPaper.get()).sync(()->onActivate.accept(context));
	}
	
	@Override
	protected void resolveAbaondonedStream() {
		if (onAbandon == null) return;
		if(Bukkit.isPrimaryThread()) onAbandon.accept(context);
		else Run.as(AtlasPaper.get()).sync(()->onAbandon.accept(context));
	}
}
