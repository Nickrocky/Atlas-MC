package com.dndcraft.atlas.menu;

import com.dndcraft.atlas.convo.ChatStream;
import com.dndcraft.atlas.util.Context;

import java.util.function.Consumer;

class MenuChatStream extends ChatStream {
	private final MenuAgent agent;
	
	public MenuChatStream(MenuAgent a) {
		super(a.getPlayer());
		agent = a;
	}

	@Override
	public void activate(Consumer<Context> go) {
		Consumer<Context> chained = c->{
			agent.mergeContext(c);
			go.accept(c);
			agent.getPlayer().openInventory(agent.getMenu().getInventory());
		};
		
		super.activate(chained);
	}
	
}
