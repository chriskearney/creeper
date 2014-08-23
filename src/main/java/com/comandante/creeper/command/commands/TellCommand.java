package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.google.common.collect.ImmutableList;

public class TellCommand extends Command {

    private final static String helpDescription = "Speak to another player in private.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "tell".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public TellCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        getGameManager().tell(getGameManager().getPlayerManager().getPlayer(getPlayerId()), getOriginalMessage());
    }
}
