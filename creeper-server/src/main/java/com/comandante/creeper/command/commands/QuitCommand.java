package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class QuitCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("quit");
    final static String description = "Quit the game";
    final static String correctUsage = "quit";

    public QuitCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (player.getActiveFights().size() > 0) {
                write("You can't quit in the middle of a fight!");
            } else {
                gameManager.getPlayerManager().removePlayer(creeperSession.getUsername().get());
            }
        });
    }
}
