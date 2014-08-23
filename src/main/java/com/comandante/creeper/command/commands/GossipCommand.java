package com.comandante.creeper.command.commands;


import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import com.google.common.collect.ImmutableList;

public class GossipCommand extends Command {

    private final static String helpDescription = "Speak to the entire server.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "gossip".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public GossipCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        getGameManager().gossip(player, getOriginalMessage().replaceFirst("^gossip ", ""));
    }
}
