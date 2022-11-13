package com.dndcraft.atlas.agnostic;

import net.kyori.adventure.text.Component;

import java.util.stream.Stream;

public interface Sender {

    boolean hasPermission(String perm);

    String getName();

    void sendMessage(String msg);

    void sendMessage(Component msg);

    default void sendMessage(Component... msg) {
        Stream.of(msg).forEach(this::sendMessage);
    }

}
