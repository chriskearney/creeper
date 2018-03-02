package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.pircbotx.MultiBotManager;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BounceIrcBotCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("bounce");
    final static String description = "Restart IRC Bot.";
    final static String correctUsage = "bounce";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.ADMIN);

    public BounceIrcBotCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommandBackgroundThread(ctx, e, () -> {
            try {
                MultiBotManager manager = gameManager.getIrcBotService().getManager();
                write("IRC Bot Service shutting down.\r\n");
                manager.stopAndWait();
                write("IRC Bot Service stopped.\r\n");
                MultiBotManager multiBotManager = gameManager.getIrcBotService().newBot();
                multiBotManager.start();
                gameManager.getIrcBotService().setManager(multiBotManager);
                write("IRC Bot Service started.\r\n");
            } catch (Exception ex) {
                log.error("Unable to restart IRC service", ex);
            }
        });
    }
}