package com.comandante.creeper.merchant.bank.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BankCommandRegistry {

    private final BankCommand unknownCommand;

    public BankCommandRegistry(BankCommand unknownCommand) {
        this.unknownCommand = unknownCommand;
    }

    private final HashMap<String, BankCommand> bankCommands = new HashMap<>();

    public void addCommand(BankCommand command) {
        List<String> validTriggers = command.validTriggers;
        for (String trigger: validTriggers) {
            bankCommands.put(trigger, command);
        }
    }

    public BankCommand getCommandByTrigger(String trigger) {
        for (Map.Entry<String, BankCommand> next : bankCommands.entrySet()) {
            if (trigger.equals(next.getKey())) {
                return next.getValue();
            }
        }
        return unknownCommand;
    }

    public Set<BankCommand> getBankCommands() {
        Set<BankCommand> creeperCommandUniq = new HashSet<BankCommand>(bankCommands.values());
        return creeperCommandUniq;
    }
}