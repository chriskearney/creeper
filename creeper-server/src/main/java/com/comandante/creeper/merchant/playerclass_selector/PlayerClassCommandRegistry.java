package com.comandante.creeper.merchant.playerclass_selector;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerClass;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerClassCommandRegistry {

    private final PlayerClassCommand unknownCommand;
    private final GameManager gameManager;

    public PlayerClassCommandRegistry(GameManager gameManager) {
        this.gameManager = gameManager;
        this.unknownCommand = new UnknownCommand(gameManager);
    }

    public PlayerClassCommand getCommandByTrigger(String trigger) {
        if (trigger.equalsIgnoreCase("leave")) {
            return new LeaveCommand(gameManager);
        }
        List<PlayerClass> matchedClasses = Arrays.stream(PlayerClass.values()).filter(playerClass -> playerClass.getIdentifier().equalsIgnoreCase(trigger)).collect(Collectors.toList());
        if (matchedClasses.size() > 0) {
            return new ChooseClassCommand(matchedClasses.get(0), gameManager);
        }
        return unknownCommand;
    }

}