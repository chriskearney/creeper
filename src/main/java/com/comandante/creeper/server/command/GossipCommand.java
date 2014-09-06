package com.comandante.creeper.server.command;


import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.comandante.creeper.server.Color.MAGENTA;
import static com.comandante.creeper.server.Color.RESET;

public class GossipCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("g", "gossip");
    final static String description = "Gossip to the entire server.";

    public GossipCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession session = extractCreeperSession(e.getChannel());
            String playerId = extractPlayerId(session);
            List<String> origMessageParts = getOriginalMessageParts(e);
            if (origMessageParts.size() == 1) {
                getGameManager().getChannelUtils().write(playerId, "Nothing to gossip about?");
                return;
            }
            origMessageParts.remove(0);
            final String msg = Joiner.on(" ").join(origMessageParts);
            Player sourcePlayer = getGameManager().getPlayerManager().getPlayer(playerId);
            Iterator<Map.Entry<String, Player>> players = getGameManager().getPlayerManager().getPlayers();
            while (players.hasNext()) {
                StringBuilder stringBuilder = new StringBuilder();
                Player player = players.next().getValue();
                stringBuilder.append(MAGENTA);
                stringBuilder.append("[").append(sourcePlayer.getPlayerName()).append("] ").append(msg);
                stringBuilder.append(RESET);
                if (player.getPlayerId().equals(sourcePlayer.getPlayerId())) {
                    getGameManager().getChannelUtils().write(playerId, stringBuilder.toString());
                } else {
                    getGameManager().getChannelUtils().write(player.getPlayerId(), stringBuilder.toString());
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
