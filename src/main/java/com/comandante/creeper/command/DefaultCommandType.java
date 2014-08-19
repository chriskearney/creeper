package com.comandante.creeper.command;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum DefaultCommandType implements CommandType {
    MOVE_NORTH(new HashSet<String>(Arrays.asList("n", "North")), false, "Move north."),
    MOVE_SOUTH(new HashSet<String>(Arrays.asList("s", "South")), false, "Move south."),
    MOVE_EAST(new HashSet<String>(Arrays.asList("e", "East")), false, "Move east."),
    MOVE_WEST(new HashSet<String>(Arrays.asList("w", "West")), false, "Move west."),
    SAY(new HashSet<String>(Arrays.asList("say")), true, "Say something to the current room."),
    GOSSIP(new HashSet<String>(Arrays.asList("gossip")), true, "Gossip to the entire server."),
    UNKNOWN(new HashSet<String>(Arrays.asList("gossip")), true, "Gossip to the entire server.");

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
