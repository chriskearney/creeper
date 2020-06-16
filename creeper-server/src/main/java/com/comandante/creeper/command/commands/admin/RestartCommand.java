package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RestartCommand extends Command {



    final static List<String> validTriggers = Arrays.asList("restart");
    final static String description = "restart server.";
    final static String correctUsage = "restart";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);

    public RestartCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommandThreadSafe(ctx, e, BounceIrcBotCommand.class, () -> {
            playerManager.getAllPlayersMap().values().stream()
                    .filter(player -> player.getChannel().isConnected())
                    .forEach(player -> gameManager.getChannelUtils().write(player.getPlayerId(),
                            "                      88                                          \n" +
                    "                      88                                   ,d     \n" +
                    "                      88                                   88     \n" +
                    "8b,dPPYba,  ,adPPYba, 88,dPPYba,   ,adPPYba,   ,adPPYba, MM88MMM  \n" +
                    "88P'   \"Y8 a8P_____88 88P'    \"8a a8\"     \"8a a8\"     \"8a  88     \n" +
                    "88         8PP\"\"\"\"\"\"\" 88       d8 8b       d8 8b       d8  88     \n" +
                    "88         \"8b,   ,aa 88b,   ,a8\" \"8a,   ,a8\" \"8a,   ,a8\"  88,    \n" +
                    "88          `\"Ybbd8\"' 8Y\"Ybbd8\"'   `\"YbbdP\"'   `\"YbbdP\"'   \"Y888  \n" +
                    "                                                                  "));
            gameManager.getMapDBCreeperStorage().stopAsync();
            gameManager.getMapDBCreeperStorage().awaitTerminated();
            System.exit(0);
        });
    }
}