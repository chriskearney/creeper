package com.comandante.creeper.server;

import com.comandante.creeper.server.command.Command;

import java.util.*;

public class CreeperCommandRegistry {

    private final Command unknownCommand;

    public CreeperCommandRegistry(Command unknownCommand) {
        this.unknownCommand = unknownCommand;
    }

    private final HashMap<String, Command> creeperCommands = new HashMap<>();

    public void addCommand(Command command) {
        List<String> validTriggers = command.validTriggers;
        for (String trigger: validTriggers) {
            creeperCommands.put(trigger, command);
        }
    }

    public Command getCommandByTrigger(String trigger) {
        for (Map.Entry<String, Command> next : creeperCommands.entrySet()) {
            if (trigger.equals(next.getKey())) {
                return next.getValue();
            }
        }
        return unknownCommand;
    }

    public Set<Command> getCreeperCommands() {
        Set<Command> creeperCommandUniq = new HashSet<Command>(creeperCommands.values());
        return creeperCommandUniq;
    }
}
