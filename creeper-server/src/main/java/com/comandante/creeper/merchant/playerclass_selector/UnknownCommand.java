package com.comandante.creeper.merchant.playerclass_selector;


import com.comandante.creeper.core_game.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

public class UnknownCommand extends PlayerClassCommand {

    public UnknownCommand(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            write(getPrompt());
            e.getChannel().getPipeline().remove("executed_command");
            e.getChannel().getPipeline().remove("executed_playerclass_command");
        } finally {

        }
    }
}
