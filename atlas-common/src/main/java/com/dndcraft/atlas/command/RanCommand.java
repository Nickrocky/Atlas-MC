package com.dndcraft.atlas.command;

import com.dndcraft.atlas.Atlas;
import com.dndcraft.atlas.agnostic.Sender;
import com.dndcraft.atlas.util.AtlasColor;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class RanCommand implements CommandHandle{

    public  static final Component ERROR_PREFIX = Component.text("[Error] ", AtlasColor.RED.toTextColor());
    public  static final Component ERROR_UNSPECIFIED = Component.text(" An unhandled error occurred when processing the command.", AtlasColor.RED.toTextColor());
    private static final Component ERROR_FLAG_ARG = Component.text("Not a valid flag argument provided for: ", AtlasColor.RED.toTextColor());
    private static final Component DUPLICATE_FLAG = Component.text("You've provided a duplicate flag: ");
    private static final Component ERROR_SENDER_UNRESOLVED = Component.text("This command can only be ran for: ");

    final AtlasCommand command;
    final String usedAlias;

    final Sender sender;
    Object resolvedSender;

    List<Object> argResults = new ArrayList<>();
    Map<String, Object> context = new HashMap<>();
    Map<String, Object> flags = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T getArg(int i) { //Static typing is for PUSSIES
        return (T) argResults.get(i);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getFlag(String flagName) {
        return (T) flags.get(flagName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContext(String key) {
        return (T) context.get(key);
    }

    @Override
    public boolean hasFlag(String flagName) {
        return flags.containsKey(flagName);
    }

    public void addContext(String key, Object value) {
        context.put(key, value);
    }

    @Deprecated
    @Override
    public void msg(String message, Object... format) {
        String formatted = String.format(message, format);
        sender.sendMessage(formatted);
    }

    @Override
    public void msg(Component message) {
        sender.sendMessage(message);
    }

    @Deprecated
    @Override
    public void msg(Object o) {
        msgRaw(String.valueOf(o));
    }

    @Deprecated
    public void msgFormat(String message, Object... format) {
        String formatted = String.format(message, format);
        sender.sendMessage(formatted);
    }

    @Deprecated
    @Override
    public void msgRaw(String message) {
        sender.sendMessage(message);
    }

    void parseAll(List<String> args) {
        parseFlags(args);
        if(hasFlag("h") && command.getHelp() != null) {
            Atlas.get().getLogger().info("Found a help flag! No further parsing needed!");
            return;
        }
        parseCommandSender();
        parseArgs(args);

        Atlas.get().getLogger().info("Parsed " + argResults.size() + " args and " + flags.size() + " flags.");
    }

    private <S> void parseCommandSender() throws CmdParserException {
        @SuppressWarnings("unchecked") //Always allowed, doesnt confine anything yet
                ParameterType<S> senderType = (ParameterType<S>) command.getSenderType();
        if(senderType != null) {
            if(hasFlag("sudo")) {
                resolvedSender = getFlag("sudo");
            } else {
                S resolved = senderType.senderMapper().apply(sender);
                if(resolved != null && (senderType.filter() == null || senderType.filter().test(resolved)) ) {
                    resolvedSender = resolved;
                    flags.put("sudo", resolved);
                } else {
                    error(ERROR_SENDER_UNRESOLVED.append(Component.text(senderType.getDefaultName())));
                }
            }
        } else { //No custom sender type required
            resolvedSender = sender;
        }
        Validate.notNull(resolvedSender);
    }

    void handleException(Exception e) {
        if(e instanceof CmdParserException || e.getCause() instanceof CmdParserException) {
            String err = e.getMessage();
            if(StringUtils.isEmpty(err)) Atlas.get().getLogger().info("An empty CmdParserException for command: " + usedAlias + " from " + sender + ". This might be intentional.");
            else msg(ERROR_PREFIX.append(Component.text(e.getMessage())));
        } else {
            msg(ERROR_PREFIX.append(ERROR_UNSPECIFIED));
            e.printStackTrace();
        }
    }

    private void parseFlags(List<String> args) throws CmdParserException {
        //Father forgive me for I have sinned
        List<CmdFlag> f = command.getFlags().stream().filter(fl->fl.mayUse(sender))
                .collect(Collectors.toCollection(ArrayList::new));
        for(int i = 0; i < args.size(); i++) {
            String a = args.get(i);
            if(a.startsWith("-")) {
                CmdFlag flag = matchFlag(a, f);
                if(flag != null) { //Weird shit happens to i here. Plan accordingly.
                    args.remove(i--); //this shortens the list of args. Index shift

                    if(flag.isVoid()) { //Some flags cant possibly take any arguments. Give special treatment for client flexibility
                        putFlag(flag, "I_EXIST");
                        continue;
                    }

                    String flagArg;
                    if((i+1) < args.size() && !args.get(i+1).startsWith("-")) { //next arg is now under the cursor
                        flagArg = args.remove(1+i); //Additional index shift since we took 2 args now
                    } else {
                        flagArg = flag.getArg().getDefaultInput();
                    }

                    if (flagArg == null) error(ERROR_FLAG_ARG.append(Component.text(flag.getName())));

                    Object resolved = Optional.of(flagArg).map(xx->flag.getArg().resolve(sender, xx)).orElse(null);
                    if(resolved == null) error(ERROR_FLAG_ARG.append(Component.text(flag.getName())));
                    else putFlag(flag, resolved);
                }
            }
        }
    }

    private void putFlag(CmdFlag flag, Object resolved) {
        if(flags.containsKey(flag.getName())) error(Component.text(DUPLICATE_FLAG + flag.getName()));
        else flags.put(flag.getName(), resolved);
    }

    private CmdFlag matchFlag(String input, List<CmdFlag> flags) {
        String xput = input.substring(1).toLowerCase();
        for(CmdFlag flag : flags) {
            boolean flagFound = flag.getAliases().stream().filter(f->f.equals(xput)).findAny().isPresent();
            if(flagFound) return flag;
        }

        return null;
    }

    private void parseArgs(List<String> args) throws CmdParserException {
        List<CmdArg<?>> cmdArgs = command.getArgs();

        HelpCommand help = command.getHelp();
        if(args.size() == 0 && cmdArgs.size() > 0 && !cmdArgs.get(0).hasDefaultInput() && help != null) {
            Atlas.get().getLogger().info("Found 0 args for a command that takes more. Defaulting to help output.");
            flags.put("h", 0);
            return;
        }

        for(int i = 0; i < cmdArgs.size(); i++) {
            CmdArg<?> arg = cmdArgs.get(i);
            Object o = null;
            if(i >= args.size()) o = arg.resolveDefault(sender);
            else o = arg.resolve(sender, args, i);

            if(o == null) error(Component.text("at argument " + (i+1) + ": ").append(arg.getErrorMessage()));
            else argResults.add(o);
        }
    }

    @Override
    public void error(Component err) {
        throw new CmdParserException(err);
    }

    @Override
    public void validate(boolean condition, Component error) {
        if(!condition) error(error);
    }

    static class CmdParserException extends RuntimeException{
        private static final long serialVersionUID = 5283812808389224035L;

        private CmdParserException(Component err) {
            super(PlainComponentSerializer.plain().serialize(err));
        }
    }

}
