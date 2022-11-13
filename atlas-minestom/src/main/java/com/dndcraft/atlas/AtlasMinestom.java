package com.dndcraft.atlas;

import com.dndcraft.atlas.agnostic.AbstractComponentBuilder;

import java.io.File;
import java.util.logging.Logger;

public class AtlasMinestom implements Atlas{
    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public AbstractComponentBuilder<?> componentBuilder() {
        return null;
    }
}
