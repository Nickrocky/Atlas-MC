package com.dndcraft.atlas;

import com.dndcraft.atlas.command.BrigadierProvider;
import com.dndcraft.atlas.command.ItemArg;
import com.dndcraft.atlas.command.SenderTypes;
import com.dndcraft.atlas.command.brigadier.CommandNodeManager;
import com.dndcraft.atlas.io.mongodb.AtlasStorage;
import com.dndcraft.atlas.item.RestrictionListener;
import com.dndcraft.atlas.menu.MenuListener;
import com.dndcraft.atlas.util.BukkitComponentBuilder;
import com.dndcraft.atlas.util.PlayerUtil;
import com.dndcraft.atlas.util.Run;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class AtlasPaper extends JavaPlugin implements Atlas {

    public static AtlasPaper get(){ return (AtlasPaper) Atlas.get();}

    @Override
    public void onLoad() {
        InstanceProvider.init(this);
    }

    @Override
    public void onEnable() {
        registerCommandParameterTypes();
        listen(new MenuListener());
        listen(new RestrictionListener());
        listen(new PlayerUtil());

        Run.as(this).delayed(2, ()->{ //Brigadier singleton deep inside NMS: get and inject
            CommandNodeManager.getInstance().inject(BrigadierProvider.get().getBrigadier().getRoot());
        });
    }

    public void registerSerializable(){

    }

    @Override
    public void onDisable() {
        AtlasStorage.stop();
    }

    @Override
    public BukkitComponentBuilder componentBuilder() {
        return new BukkitComponentBuilder();
    }

    //todo add AtlasColor
    private void registerCommandParameterTypes() {
        SenderTypes.registerCommandSenderType();
        SenderTypes.registerPlayerType();
        SenderTypes.registerOfflinePlayerType();

        ItemArg.buildItemStackParameter();
        ItemArg.buildMaterialParameter();
    }

    private void listen(Listener l) {
        Bukkit.getPluginManager().registerEvents(l, this);
    }

}
