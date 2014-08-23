package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import com.google.common.collect.ImmutableList;

public class WhoCommand extends Command {

    private final static String helpDescription = "List players currently logged in to server.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "whoami".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public WhoCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        player.getChannel().write(player.getPlayerName() + "\r\n");
    }
}
