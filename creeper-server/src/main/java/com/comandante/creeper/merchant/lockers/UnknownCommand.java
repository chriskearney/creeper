package com.comandante.creeper.merchant.lockers;


import com.comandante.creeper.core_game.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

public class UnknownCommand extends LockerCommand {

    public UnknownCommand(GameManager gameManager) {
        super(gameManager, null, null);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            write(getPrompt());
            e.getChannel().getPipeline().remove("executed_command");
            e.getChannel().getPipeline().remove("executed_locker_command");
        } finally {

        }
    }
}
