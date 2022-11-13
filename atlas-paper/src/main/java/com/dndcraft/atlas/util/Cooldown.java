package com.dndcraft.atlas.util;

import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@FieldDefaults(level=AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Builder(builderClassName="Builder")
public class Cooldown {
	Plugin plugin;
	@Default
    static TimeUnit timeUnit = TimeUnit.SECONDS;
	long duration;
	
	Component triggerMessage, cooldownMessage, expiryMessage;
	Consumer<Player> onTrigger, onExpire;
	
	@Default
    static Map<Long, Consumer<Player>> when = Maps.newHashMap();
	@Default
    Map<UUID, CoolTask> affected = Maps.newHashMap();
	
	
	public boolean isOnCooldown(Player p) {
		return affected.containsKey(p.getUniqueId());
	}
	
	public Duration getCooldown(Player p) {
		val cool = affected.get(p.getUniqueId());
		if(cool == null) return Duration.ZERO;
		
		return Duration.between(Instant.now(), cool.expires);
	}
	
	public long getCooldownTicks(Player p) {
		return getCooldown(p).toMillis() / 50l;
	}
	
	public boolean informOrTriggerCooldown(Player p) {
		UUID u = p.getUniqueId();
		if(affected.containsKey(u)) {
			if(cooldownMessage != null) p.sendMessage(cooldownMessage);
			return true;
		} else {
			go(u);
			return false;
		}
	}
	
	public void trigger(Player p) {
		UUID u = p.getUniqueId();
		if(affected.containsKey(u)) affected.get(u).cancel();
		go(u);
	}
	
	private void go(UUID u) {
		long expires = System.currentTimeMillis() + timeUnit.toMillis(duration);
		
		val coolTask = new CoolTask();
		coolTask.expires = Instant.ofEpochMilli(expires);
		affected.put(u, coolTask);
		
		doExecute(u, triggerMessage, onTrigger);
		
		coolTask.tasks.add(asTask(duration, ()->{
			doExecute(u, expiryMessage, onExpire);
			affected.remove(u);
		}));
		
		when.forEach( (after, task) -> coolTask.tasks.add(asTask(after, ()->doExecute(u, null, task) )));
	}
	
	
	private BukkitTask asTask(long after, Runnable r) {
		long afterTicks = timeUnit.toMillis(after) / 50;
		return new BukkitRunnable() {
			
			@Override
			public void run() {
				r.run();
			}
		}.runTaskLater(plugin, afterTicks);
	}
	
	private static void doExecute(UUID u, Component someMessage, Consumer<Player> whatToDo) {
		Player p = Bukkit.getPlayer(u);
		if(p != null) {
			if(someMessage != null) p.sendMessage(someMessage);
			if(whatToDo != null) whatToDo.accept(p);
		}
	}
	
	private static class CoolTask{
		Instant expires;
		List<BukkitTask> tasks = new ArrayList<>();
		
		private void cancel() {
			tasks.stream().filter(t->!t.isCancelled()).forEach(BukkitTask::cancel);
		}
	}
	
	public static Builder builder(Plugin plugin) {
		return new Builder().plugin(plugin);
	}
	
	public static class Builder{

		public Builder lasts(long duration, TimeUnit unit) {
			this.duration = duration;
			timeUnit = unit; //Ignore error
			return this;
		}
		
		public Builder triggerMessage(String message) {
			return triggerMessage(component(message));
		}
		
		public Builder triggerMessage(Component message) {
			this.triggerMessage = message;
			return this;
		}
		
		public Builder expiryMessage(String message) {
			return expiryMessage(component(message));
		}
		
		public Builder expiryMessage(Component message) {
			this.expiryMessage = message;
			return this;
		}
		
		public Builder cooldownMessage(String message) {
			return cooldownMessage(component(message));
		}
		
		public Builder cooldownMessage(Component message) {
			this.cooldownMessage = message;
			return this;
		}
		
		private Component component(String message) {
			return Component.text(message);
		}
		
		//Blocks these internal maps from being fucked up at the builder level
		@SuppressWarnings("unused") private void when(Map<?,?> any) { }
		@SuppressWarnings("unused") private void affected(Map<?,?> any) { }
		
		public Builder after(long duration, Consumer<Player> task) {
			when.put(duration, task); //Ignore error
			return this;
		}
		
	}

}
