package com.comandante.creeper.command;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum DefaultCommandType implements CommandType {
    MOVE_NORTH(new HashSet<String>(Arrays.asList("n", "north")), false, "Move north."),
    MOVE_SOUTH(new HashSet<String>(Arrays.asList("s", "south")), false, "Move south."),
    MOVE_EAST(new HashSet<String>(Arrays.asList("e", "east")), false, "Move east."),
    MOVE_WEST(new HashSet<String>(Arrays.asList("w", "west")), false, "Move west."),
    SAY(new HashSet<String>(Arrays.asList("say")), false, "Say something to the current room."),
    TELL(new HashSet<String>(Arrays.asList("tell", "t")), false, "Tell something to a player in private."),
    GOSSIP(new HashSet<String>(Arrays.asList("gossip")), false, "Gossip to the entire server."),
    WHO(new HashSet<String>(Arrays.asList("who")), false, "List who is logged into the server."),
    WHOAMI(new HashSet<String>(Arrays.asList("whoami")), false, "Who am I?"),
    HELP(new HashSet<String>(Arrays.asList("help")), false, "This is the help."),
    UNKNOWN(new HashSet<String>(Arrays.asList("")), true, "");

    private Set<String> validCommandTriggers;
    private boolean caseSensitive;
    private String description;
    private String originalMessage;

    DefaultCommandType(Set<String> triggers, boolean caseSensitive, String description) {
        this.validCommandTriggers = triggers;
        this.caseSensitive = caseSensitive;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Set<String> getValidCommandTriggers() {
        return validCommandTriggers;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    @Override
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public static CommandType getCommandTypeFromMessage(String message) {
        DefaultCommandType commandType = (DefaultCommandType) CommandTypeHelper.getCommandType(DefaultCommandType.values(), message);
        commandType.setOriginalMessage(message);
        return commandType;
    }
}
