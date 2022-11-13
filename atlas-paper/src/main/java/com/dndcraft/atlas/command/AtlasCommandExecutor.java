package com.dndcraft.atlas.command;


import com.dndcraft.atlas.wrapper.BukkitSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public final class AtlasCommandExecutor extends AgnosticExecutor implements TabExecutor {

	public AtlasCommandExecutor(AtlasCommand rootCommand) {
		super(rootCommand);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		BukkitSender bukkitSender = new BukkitSender(sender);
		return super.onTabComplete(bukkitSender, alias, args);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		BukkitSender bukkitSender = new BukkitSender(sender);
		return super.onCommand(bukkitSender, label, args);
	}
}
