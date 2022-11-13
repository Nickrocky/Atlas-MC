package com.dndcraft.atlas.command;

import com.dndcraft.atlas.command.brigadier.CommandNodeManager;
import com.dndcraft.atlas.command.brigadier.Kommandant;
import com.dndcraft.atlas.command.exception.InvalidPluginCommandException;
import com.dndcraft.atlas.wrapper.BukkitCommand;
import org.bukkit.command.PluginCommand;

import java.util.function.Supplier;

public class Commands {

	/**
	 * Creates an AtlasCommandBuilder for you that parses an annotated class into a CommandExecutor
	 * @param command The PluginCommand you intend to wrap
	 * @param template A supplier for the CommandTemplate subclass you wish to use. Should Supply unique instances if intending to use Runnables
	 * @return
	 */
	public static AtlasCommandBuilder builder(PluginCommand command, Supplier<CommandTemplate> template) {
		var wrapper = new BukkitCommand(command);
		return new AnnotatedCommandParser(template, wrapper).invokeParse((handler)->{
			Kommandant kommandant = new BukkitKommandant(handler);
			kommandant.addBrigadier();
			CommandNodeManager.getInstance().register(kommandant);
			
			var pluginCommand = ((BukkitCommand) wrapper).getHandle();
			AtlasCommandExecutor executor = new AtlasCommandExecutor(handler);
			pluginCommand.setExecutor(executor);
		});
	}
	
	/**
	 * Calls {{@link #build(PluginCommand, Supplier)} .build(), finalizing the command build process
	 */
	public static void build(PluginCommand command, Supplier<CommandTemplate> template) throws InvalidPluginCommandException {
		if(command==null) throw new InvalidPluginCommandException();
		builder(command, template).build();
	}

	public static <T> ParameterType<T> defineArgumentType(Class<T> forClass){
		return new ParameterType<>(forClass);
	}
	
}
