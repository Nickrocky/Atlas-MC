package com.dndcraft.atlas.command;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.AtlasPaper;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class CommandsPacketIntercept {
	private final AtlasPaper plugin;
	
	private final List<CommandNode<Object>> nodes = Collections.synchronizedList(new ArrayList<>());
	RootCommandNode<?> lastSeen = null;
	
	
	public void injectNode(CommandNode<Object> node) {
		nodes.add(node);
	}
	
	public void startListening() {
		//ProtocolLibrary.getProtocolManager().getAsynchronousManager().registerAsyncHandler(
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(plugin, PacketType.Play.Server.COMMANDS) {
					@Override
					public void onPacketSending(final PacketEvent event) {
						@SuppressWarnings("unchecked")
						var root = (RootCommandNode<Object>) event.getPacket().getModifier().read(0);

						if(lastSeen == root) Atlas.get().getLogger().severe("ITS A SINGLETON!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						lastSeen = root;

						for(var node : nodes) {
							String name = node.getName();
							despigot(root, name);
							root.addChild(node);
						}
					}
				});//.start();
	}
	
	private void despigot(RootCommandNode<Object> root, String alias) {
		var iter = root.getChildren().iterator();
		while(iter.hasNext()) {
			var kid = iter.next(); //Search spigot's attempt at registering the argument
			if(kid.getName().equals(alias))
				iter.remove(); //Killing the skeletal framework of spigot gives us full Brigadier power
		}
	}
	
	
}
