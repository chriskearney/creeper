package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.RecentChangesManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RecentChangesCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("recentchanges");
    final static String description = "Print the recent changes to the creeper codebase.";
    final static String correctUsage = "recentchanges";

    public RecentChangesCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommandBackgroundThread(ctx, e, () -> {
            try {
                write(RecentChangesManager.getRecentChanges());
            } catch (ExecutionException ex) {
                log.error("Unable to retrieve recent changes.", ex);
            }
        });
    }
}
