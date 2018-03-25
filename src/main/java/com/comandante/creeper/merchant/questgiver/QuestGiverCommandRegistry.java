package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.lockers.LockerCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestGiverCommandRegistry {
    private final QuestGiverCommand unknownCommand;
    private final GameManager gameManager;

    public QuestGiverCommandRegistry(GameManager gameManager) {
        this.gameManager = gameManager;
        this.unknownCommand = new UnknownCommand(gameManager);
    }

    private final HashMap<String, QuestGiverCommand> questGiverCommands = new HashMap<>();

    public void addCommand(QuestGiverCommand command) {
        List<String> validTriggers = command.validTriggers;
        for (String trigger : validTriggers) {
            questGiverCommands.put(trigger, command);
        }
    }

    public QuestGiverCommand getCommandByTrigger(String trigger) {
        for (Map.Entry<String, QuestGiverCommand> next : questGiverCommands.entrySet()) {
            if (trigger.equals(next.getKey())) {
                return next.getValue();
            }
        }
        return unknownCommand;
    }
}
