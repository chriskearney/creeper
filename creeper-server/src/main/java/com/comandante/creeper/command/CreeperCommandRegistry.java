package com.comandante.creeper.command;

import com.comandante.creeper.command.commands.Command;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
