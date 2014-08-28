package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.google.common.collect.ImmutableList;

public class UnknownCommand extends Command {
    private final static String helpDescription = "Unknown.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public UnknownCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        getGameManager().getChannelUtils().writeOnlyPrompt(getPlayerId());
    }
}
