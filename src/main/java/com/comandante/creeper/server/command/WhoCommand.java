package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.comandante.creeper.server.Color.CYAN;
import static com.comandante.creeper.server.Color.RESET;

public class WhoCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("who");
    final static String description = "Drop an item";

    public WhoCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            Set<Player> allPlayers = getGameManager().getAllPlayers();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(CYAN);
            stringBuilder.append("----------------------\r\n");
            stringBuilder.append("|--active users------|\r\n");
            stringBuilder.append("----------------------\r\n");
            for (Player allPlayer : allPlayers) {
                stringBuilder.append(allPlayer.getPlayerName());
                stringBuilder.append(" - ").append(allPlayer.getChannel().getRemoteAddress().toString());
                stringBuilder.append("\r\n");
            }
            stringBuilder.append(RESET);
            getGameManager().getChannelUtils().write(getPlayerId(getCreeperSession(e.getChannel())), stringBuilder.toString());
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
