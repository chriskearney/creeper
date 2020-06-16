package com.comandante.creeper.merchant.lockers;

import java.util.*;

public class LockerCommandRegistry {

    private final LockerCommand unknownCommand;

    public LockerCommandRegistry(LockerCommand unknownCommand) {
        this.unknownCommand = unknownCommand;
    }

    private final HashMap<String, LockerCommand> lockerCommands = new HashMap<>();

    public void addCommand(LockerCommand command) {
        List<String> validTriggers = command.validTriggers;
        for (String trigger: validTriggers) {
            lockerCommands.put(trigger, command);
        }
    }

    public LockerCommand getCommandByTrigger(String trigger) {
        for (Map.Entry<String, LockerCommand> next : lockerCommands.entrySet()) {
            if (trigger.equals(next.getKey())) {
                return next.getValue();
            }
        }
        return unknownCommand;
    }

    public Set<LockerCommand> getLockerCommands() {
        Set<LockerCommand> creeperCommandUniq = new HashSet<LockerCommand>(lockerCommands.values());
        return creeperCommandUniq;
    }
}