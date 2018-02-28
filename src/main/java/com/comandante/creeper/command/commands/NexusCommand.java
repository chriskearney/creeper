package com.comandante.creeper.command.commands;


import com.comandante.creeper.core_game.GameManager;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class NexusCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("nexus", "nx");
    final static String description = "Say something in #nexus";
    final static String correctUsage = "nexus <words>";

    public NexusCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description,correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (gameManager.getCreeperConfiguration().isIrcEnabled()) {
                originalMessageParts.remove(0);
                String transferPhrase = Joiner.on(" ").join(originalMessageParts);
                if (gameManager.getIrcBotService().getBot().isConnected()) {
                    gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(player.getPlayerName() + ": " + transferPhrase);
                }
            }
        });
    }
}