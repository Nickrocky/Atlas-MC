package com.dndcraft.atlas.wrapper;

import com.dndcraft.atlas.agnostic.AgnosticObject;
import com.dndcraft.atlas.agnostic.Sender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

@RequiredArgsConstructor
public class BukkitSender implements Sender, AgnosticObject<CommandSender> {
	
	@Getter
	private final CommandSender handle;

	@Override
	public boolean hasPermission(String perm) {
		return handle.hasPermission(perm);
	}

	@Override
	public String getName() {
		return handle.getName();
	}

	@Override
	public void sendMessage(String msg) {
		handle.sendMessage(msg);
	}

	@Override
	public void sendMessage(Component msg) {
		handle.sendMessage(msg);
	}

	@Override
	public void sendMessage(Component... msg) {
		for(Component c : msg) {
			handle.sendMessage(c);
		}
	}
}
