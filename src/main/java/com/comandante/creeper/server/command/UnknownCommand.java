package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.server.CreeperSession;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

public class UnknownCommand extends Command {

    public UnknownCommand(GameManager gameManager) {
        super(gameManager, null, null);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession creeperSession = extractCreeperSession(e.getChannel());
            String playerId = getPlayerId(creeperSession);
            getGameManager().getChannelUtils().write(playerId, getGameManager().getPlayerManager().buildPrompt(playerId), false);
            e.getChannel().getPipeline().remove(ctx.getHandler());
        } finally {
            //super.messageReceived(ctx, e);
        }
    }
}
