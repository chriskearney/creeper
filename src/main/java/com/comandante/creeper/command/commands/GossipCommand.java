package com.comandante.creeper.command.commands;


import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.google.common.collect.ImmutableList;

import java.util.Iterator;
import java.util.Map;

import static com.comandante.creeper.server.Color.MAGENTA;
import static com.comandante.creeper.server.Color.RESET;

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
        Player sourcePlayer = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        Iterator<Map.Entry<String, Player>> players = getGameManager().getPlayerManager().getPlayers();
        while (players.hasNext()) {
            StringBuilder stringBuilder = new StringBuilder();
            Player player = players.next().getValue();
            stringBuilder.append(MAGENTA);
            stringBuilder.append("[").append(sourcePlayer.getPlayerName()).append("] ").append(getOriginalMessage());
            stringBuilder.append(RESET);
            if (player.getPlayerId().equals(sourcePlayer.getPlayerId())) {
                commandWrite(stringBuilder.toString());
            } else {
                getGameManager().getChannelUtils().writeNoPrompt(player.getPlayerId(), stringBuilder.toString());
            }
        }
    }
}
