package com.comandante.creeper.command.commands;


import com.comandante.creeper.bot.command.commands.BotCommand;
import com.comandante.creeper.core_game.GameManager;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GossipCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("gossip", "g");
    final static String description = "Sends a message to the entire MUD.";
    final static String correctUsage = "gossip <message>";

    public GossipCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (!player.isChatModeOn()) {
                if (originalMessageParts.size() == 1) {
                    write("Nothing to gossip about?");
                    return;
                }
                originalMessageParts.remove(0);
            }
            String msg = Joiner.on(" ").join(originalMessageParts);
            gameManager.gossip(player, msg);
        });
    }
}
