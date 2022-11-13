package com.dndcraft.atlas.wrapper;

import com.dndcraft.atlas.agnostic.AgnosticObject;
import com.dndcraft.atlas.agnostic.Command;
import com.dndcraft.atlas.agnostic.PluginOwned;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class BukkitCommand implements AgnosticObject<PluginCommand>, Command, PluginOwned<Plugin> {

	@Getter
	@Delegate(types=Command.class,excludes=CommandSender.Spigot.class)
	private final PluginCommand handle;

	@Override
	public Plugin getPlugin() {
		return handle.getPlugin();
	}
}
