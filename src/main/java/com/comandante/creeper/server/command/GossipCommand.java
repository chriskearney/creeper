package com.comandante.creeper.server.command;


import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
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
        configure(e);
        try {
            if (originalMessageParts.size() == 1) {
                write("Nothing to gossip about?");
                return;
            }
            originalMessageParts.remove(0);
            final String msg = Joiner.on(" ").join(originalMessageParts);
            Iterator<Map.Entry<String, Player>> players = playerManager.getPlayers();
            while (players.hasNext()) {
                final Player next = players.next().getValue();
                final String gossipMessage = new StringBuilder()
                        .append(MAGENTA).append("[")
                        .append(player.getPlayerName()).append("] ")
                        .append(msg).append(RESET)
                        .toString();
                if (next.getPlayerId().equals(playerId)) {
                    write(gossipMessage);
                } else {
                    channelUtils.write(next.getPlayerId(), gossipMessage + "\r\n", true);
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
