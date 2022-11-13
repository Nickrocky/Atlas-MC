package com.dndcraft.atlas;

import com.dndcraft.atlas.agnostic.CommonComponentBuilder;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

@Plugin(id = "atlasvelocity",
name = "AtlasVelocity", version = "${project.version}",
description = "Atlas API but Velocity this time",
authors = {"Nickrocky213"}
)
public class AtlasVelocity implements Atlas {

    public static AtlasVelocity get(){return (AtlasVelocity) Atlas.get();}

    @Override
    public File getDataFolder() {
        return dataDirectory.toFile();
    }

    @Override
    public java.util.logging.Logger getLogger() {
        return (java.util.logging.Logger) logger;
    }

    @Override
    public CommonComponentBuilder componentBuilder() {
        return new CommonComponentBuilder();
    }

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    @Inject
    public AtlasVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory){
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        InstanceProvider.init(this);
    }

    @Subscribe
    public void onEnable(ProxyInitializeEvent e){

    }

}
