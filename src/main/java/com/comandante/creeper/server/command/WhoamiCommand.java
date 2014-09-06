package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class WhoamiCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("whoami");
    final static String description = "Who am I?";

    public WhoamiCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            Player player = getGameManager().getPlayerManager().getPlayer(extractPlayerId(extractCreeperSession(e.getChannel())));
            getGameManager().getChannelUtils().write(player.getPlayerId(), player.getPlayerName());
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
