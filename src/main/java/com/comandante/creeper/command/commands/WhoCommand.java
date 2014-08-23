package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.google.common.collect.ImmutableList;

public class WhoCommand extends Command {

    private final static String helpDescription = "List who you are.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "who".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public WhoCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        getGameManager().who(getGameManager().getPlayerManager().getPlayer(getPlayerId()));
    }
}
