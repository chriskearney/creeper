package com.comandante.creeper.server;

import com.comandante.creeper.managers.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.UUID;

public class MultiLineInputHandler extends SimpleChannelUpstreamHandler {
    private final GameManager gameManager;

    public MultiLineInputHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession creeperSession = (CreeperSession) e.getChannel().getAttachment();
            String message = (String) e.getMessage();
            if (message.equalsIgnoreCase("DONE")) {
                e.getChannel().getPipeline().addLast(UUID.randomUUID().toString(), creeperSession.getGrabMultiLineInput().get().getValue());
                return;
            }
            gameManager.getMultiLineInputManager().addToMultiLine(creeperSession.getGrabMultiLineInput().get().getKey(), message + "\r\n");
        } finally {
            e.getChannel().getPipeline().remove(ctx.getHandler());
            super.messageReceived(ctx, e);
        }
    }
}
