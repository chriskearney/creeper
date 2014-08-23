package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class Command implements Runnable {

    private final String playerId;
    private final GameManager gameManager;
    private final String helpDescription;
    private final ImmutableList validTriggers;
    private final boolean isCaseSensitiveTriggers;
    private final ArrayList<String> originalMessageParts;
    private final String originalMessage;

    public Command(String playerId, GameManager gameManager, String helpDescription, ImmutableList validTriggers, boolean isCaseSensitiveTriggers, String originalMessage) {
        this.playerId = playerId;
        this.gameManager = gameManager;
        this.helpDescription = helpDescription;
        this.validTriggers = validTriggers;
        this.isCaseSensitiveTriggers = isCaseSensitiveTriggers;
        this.originalMessageParts = getMessageParts(originalMessage);
        this.originalMessage = originalMessage;
    }

    public ArrayList<String> getOriginalMessageParts() {
        return originalMessageParts;
    }

    public String getPlayerId() {
        return playerId;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public String getHelpDescription() {
        return helpDescription;
    }

    public ImmutableList getValidTriggers() {
        return validTriggers;
    }

    public boolean isCaseSensitiveTriggers() {
        return isCaseSensitiveTriggers;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    private ArrayList<String> getMessageParts(String s) {
        String[] split = s.split(" ");
        return new ArrayList<String>(Arrays.asList(split));
    }
}
