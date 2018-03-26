package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class QuestsCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("quests", "q");
    final static String description = "Manage your quests.";
    final static String correctUsage = "quests";

    public QuestsCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            write(player.getQuestsMenu());
        });
    }



}