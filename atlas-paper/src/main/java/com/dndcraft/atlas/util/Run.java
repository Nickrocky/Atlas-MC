package com.dndcraft.atlas.util;

import com.dndcraft.atlas.agnostic.PluginOwned;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor(access=AccessLevel.PUBLIC)
public class Run implements PluginOwned<Plugin> {
	@Getter private final Plugin plugin;

	/**
	 * Checks if Run is running in sync with the main thread for the server
	 * @throws IllegalArgumentException if Validate#isTrue returns false
	 * */
	public static void ensureSync() {
		Validate.isTrue(Bukkit.isPrimaryThread());
	}

	/**
	 * Checks if Run is running async from the main thread for the server
	 * @throws IllegalArgumentException if Validate#isTrue returns false
	 * */
	public static void ensureAsync() {
		Validate.isTrue(!Bukkit.isPrimaryThread());
	}

	/**
	 * Creates a new task as a provided plugin
	 * @param plugin The plugin to run the task on behalf of
	 * @return A new instance of this class
	 * */
	public static Run as(Plugin plugin) {
		return new Run(plugin);
	}

	/**
	 * Creates a new synchronous task and returns its executor
	 * @return the executor of the sync task created
	 * */
	public Executor syncExecutor() {
		return (r->scheduler().runTask(plugin, r));
	}

	/**
	 * Creates a new asynchronous task and returns its executor
	 * @return the executor of the async task created
	 * */
	public Executor asyncExecutor() {
		return (r->scheduler().runTaskAsynchronously(plugin, r));
	}

	/**
	 * Creates a new delayed execution synchronous task
	 * @param delay time in ticks to delay by
	 * @param r the runnable/actions to occur after the delay elapses
	 * */
	public void delayed(long delay, Runnable r) {
		scheduler().runTaskLater(plugin, r, delay);
	}

	/**
	 * Creates a new delayed execution synchronous task
	 * @param timer time in ticks to repeat
	 * @param r the runnable/actions to occur after the delay elapses
	 * */
	public void repeating(long timer, Runnable r) {
		repeating(1, timer, r);
	}
	
	public void repeating(long delay, long timer, Runnable r) {
		scheduler().runTaskTimer(plugin, r, delay, timer);
	}
	
	public void sync(Runnable r) {
		scheduler().runTask(plugin, r);
	}
	
	public void async(Runnable r) {
		scheduler().runTaskAsynchronously(plugin, r);
	}
	
	public CompletableFuture<Void> future(Runnable r) {
		return CompletableFuture.runAsync(r, asyncExecutor());
	}
	
	public <T> CompletableFuture<T> future(Supplier<T> r) {
		return CompletableFuture.supplyAsync(r, asyncExecutor());
	}
	
	public <T> AsyncRunner<T> async(Supplier<T> s) {
		return new AsyncRunner<>(plugin, s);
	}
	
	@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
	public static class AsyncRunner<T> {
		private final Plugin plugin;
		private final Supplier<T> call;
		
		private Consumer<T> consumer;
		
		public void then(Consumer<T> consumer) {
			this.consumer = consumer;
			go();
		}
		
		private void go() {
			Runnable r = ()->{
				T result = call.get();
				scheduler().runTask(plugin, ()->consumer.accept(result));
			};
			scheduler().runTaskAsynchronously(plugin, r);
		}
	}
	
	private static BukkitScheduler scheduler() {
		return Bukkit.getScheduler();
	}
	
}
