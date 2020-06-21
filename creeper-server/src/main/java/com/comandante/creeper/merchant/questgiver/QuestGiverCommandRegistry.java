package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestGiverCommandRegistry {
    private final QuestGiverCommand unknownCommand;
    private final GameManager gameManager;

    public QuestGiverCommandRegistry(GameManager gameManager) {
        this.gameManager = gameManager;
        this.unknownCommand = new UnknownCommand(null, gameManager);
    }

    private final HashMap<String, QuestGiverCommand> questGiverCommands = new HashMap<>();

    public void addCommand(QuestGiverCommand command) {
        List<String> validTriggers = command.validTriggers;
        for (String trigger : validTriggers) {
            questGiverCommands.put(trigger, command);
        }
    }

    public QuestGiverCommand getCommandByTrigger(Merchant merchant, String trigger) {
        for (Map.Entry<String, QuestGiverCommand> next : questGiverCommands.entrySet()) {
            if (trigger.equals(next.getKey())) {
                return next.getValue();
            }
        }
        unknownCommand.setMerchant(merchant);
        return unknownCommand;
    }
}
