package com.dndcraft.atlas.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * @Author Nickrocky213
 * @Date: 06/24/2021
 * A general utility class for players
 * */
public class PlayerUtil implements Listener {

    private static Map<UUID, FrozenPlayer> frozenPlayers = new HashMap<>();

     //I hate that this is the only way to do this, but the only way to normally clear chat is by having a player hit F3+D
     //but seeing as that is a client side only way to clear in-game chat this will have to do i guess
     /**
     * Clears a players chat
     * @param player player to clear the chat of
     * */
    public static void clearChat(Player player){
        for(int i = 0; i < 100; i++) player.sendMessage(Component.newline()); //pain
    }

    /**
     * Temporarily freezes player for 10 seconds
     * @param player the player you wish to freeze
     * */
    public static void freeze(Player player){
        freeze(player, 10, "Atlas");
    }

    /**
     * Temporarily freezes player for a duration of time
     * @param player the player you wish to freeze
     * @param duration the amount of time in seconds you wish to freeze them
     * */
    public static void freeze(Player player, int duration){
        freeze(player, duration, "Atlas");
    }

    /**
     * Temporarily freezes a player without a time limit, it instead relies on a toggle
     * Ex. Mod toggle
     * @param player the player you wish to freeze
     * @param toggle if they are frozen or not
     * */
    public static void freeze(Player player, boolean toggle){
        freeze(player, true, "Atlas");
    }

    public static void freeze(Player player, int duration, String reason){
        FrozenPlayer entry = new FrozenPlayer(player.getUniqueId(), false, duration, reason);
        frozenPlayers.put(player.getUniqueId(), entry);
    }

    public static void freeze(Player player, boolean toggle, String reason){
        FrozenPlayer entry = new FrozenPlayer(player.getUniqueId(), true, -1, reason);
        frozenPlayers.put(player.getUniqueId(), entry);
    }

    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent e){
        if(frozenPlayers.containsKey(e.getPlayer().getUniqueId())) {
            FrozenPlayer entry = frozenPlayers.get(e.getPlayer().getUniqueId());
            if(entry.getUnfrozen().isAfter(LocalDateTime.now())){
                frozenPlayers.remove(e.getPlayer().getUniqueId());
            }else{
                e.setCancelled(true);
                Component message = new BukkitComponentBuilder().append("You are unable to move as you have been frozen by ", AtlasColor.WARNING_TEXT_YELLOW).append(" " + entry.getSource()).build();
                e.getPlayer().sendMessage(message);
            }
        }
    }
}

@RequiredArgsConstructor
@Getter
class FrozenPlayer{
    private final UUID player;
    private final boolean toggle;
    private final int duration;
    private final String source;
    private final LocalDateTime unfrozen = LocalDateTime.now().plusSeconds(Math.max(0,duration)); //bc the duration is -1 if its a toggle
}
