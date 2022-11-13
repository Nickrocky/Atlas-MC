package com.dndcraft.atlas.command;

import com.dndcraft.atlas.agnostic.Sender;
import lombok.AccessLevel;
import lombok.Setter;
import net.kyori.adventure.text.Component;

public abstract class CommandTemplate implements CommandHandle{

    @Setter(AccessLevel.PACKAGE) RanCommand ranCommand;

    protected void invoke() {
        ranCommand.getArgResults().add(0); //Dummy help page argument. Prevents error
        ranCommand.getCommand().getHelp().execute(ranCommand);
    }

    @Override
    public Sender getSender() {
        return ranCommand.getSender();
    }

    @Override
    public void msg(String message, Object... format) {
        ranCommand.msg(message, format);
    }

    @Override
    public void msg(Component message) {
        ranCommand.msg(message);
    }

    @Override
    public void msg(Object o) {
        ranCommand.msg(o);
    }

    @Override
    public void msgRaw(String message) {
        ranCommand.msgRaw(message);
    }

    @Override
    public void error(Component err) {
        ranCommand.error(err);
    }

    public void validate(boolean condition, String error) { ranCommand.validate(condition, Component.text(error));}

    @Override
    public void validate(boolean condition, Component error) {
        ranCommand.validate(condition, error);
    }

    @Override
    public boolean hasFlag(String flagName) {
        return ranCommand.hasFlag(flagName);
    }

    @Override
    public <T> T getFlag(String flagName) {
        return ranCommand.getFlag(flagName);
    }
}
