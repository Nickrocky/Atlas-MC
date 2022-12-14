package com.dndcraft.atlas.command;

import com.dndcraft.atlas.agnostic.Sender;
import lombok.*;
import lombok.experimental.NonFinal;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Value
@NonFinal
@RequiredArgsConstructor
public class AtlasCommand {

    public static final Consumer<RanCommand> NULL_COMMAND = rc->{};

    String mainCommand;
    Set<String> aliases;
    String description;
    String permission;

    ParameterType<?> senderType;

    List<CmdArg<?>> args;
    List<CmdFlag> flags;
    List<AtlasCommand> subCommands;

    @Getter(AccessLevel.NONE) Consumer<RanCommand> payload;

    void execute(RanCommand rc) {
        payload.accept(rc);
    }

    public boolean isEmptyCommand() {
        return payload == NULL_COMMAND;
    }

    public boolean hasArgs() {
        return !args.isEmpty();
    }

    public boolean hasPermission(Sender s) {
        return StringUtils.isEmpty(permission) || s.hasPermission(permission);
    }

    public boolean hasDescription() {
        return StringUtils.isNotEmpty(description);
    }

    public boolean isAlias(String param) {
        return aliases.contains(param);
    }

    public String getBestAlias(String param) {
        for(String alias : aliases) {
            if(alias.startsWith(param)) return alias;
        }
        return null;
    }

    private boolean aliasOverlaps(AtlasCommand other) {
        return aliases.stream().anyMatch(other::isAlias);
    }

    boolean argRangeOverlaps(AtlasCommand other) { //b1 <= a2 && a1 <= b2
        return other.minArgs() <= maxArgs() && minArgs() <= other.maxArgs();
    }

    boolean collides(List<AtlasCommand> subbos) {
        return subbos.stream()
                .filter(this::argRangeOverlaps)
                .anyMatch(this::aliasOverlaps);
    }

    HelpCommand getHelp() {
        return subCommands.stream()
                .filter(HelpCommand.class::isInstance)
                .map(HelpCommand.class::cast)
                .findAny().orElse(null);
    }

    private int minArgs() {
        int i = 0;
        for(val arg : args) {
            if(arg.hasDefaultInput()) return i;
            i++;
        }

        return i;
    }

    private int maxArgs() {
        int s = args.size();
        if(s > 0 && args.get(s-1) instanceof JoinedArg) return 255;
        else if(s > 0 && args.get(s-1) instanceof ArrayArgs) return 255;
        else return s;
    }

    public boolean isInvokeOverload() {
        return mainCommand.isEmpty();
    }

    public boolean fitsArgSize(int argSize) {
        return argSize >= minArgs() && argSize <= maxArgs();
    }

    @Override
    public String toString() {
        return "AtlasCommand:" + mainCommand;
    }

}
