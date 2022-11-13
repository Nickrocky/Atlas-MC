package com.dndcraft.atlas.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CommandTemplate {

    private static final Component COMMAND_SENT_DEFAULT_EXECUTOR = Component.text("Command was successfully executed, though default executor was used please change this", NamedTextColor.RED);

    private final String name;
    private final String[] aliases;

    public CommandTemplate(@NotNull String name, @Nullable String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public CommandTemplate(@NotNull String name) {
        this.name = name;
        this.aliases = new String[0];
    }

    protected void invoke() {

    }



}
