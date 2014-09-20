package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

public class UnknownCommand extends Command {

    public UnknownCommand(GameManager gameManager) {
        super(gameManager, null, null);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            write(getPrompt(), false);
            e.getChannel().getPipeline().remove(ctx.getHandler());
        } finally {
        }
    }
}
