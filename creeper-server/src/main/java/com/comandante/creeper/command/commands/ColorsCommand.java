package com.comandante.creeper.command.commands;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.server.player_communication.Color;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class ColorsCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("colors");
    final static String description = "Display available color examples.";
    final static String correctUsage = "colors";

    public ColorsCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            write("BLACK: " + Color.BLACK + "This is an example of the color." + Color.RESET + "\r\n");
            write("BLUE: " + Color.BLUE + "This is an example of the color." + Color.RESET + "\r\n");
            write("CYAN: " + Color.CYAN + "This is an example of the color." + Color.RESET + "\r\n");
            write("GREEN: " + Color.GREEN + "This is an example of the color." + Color.RESET + "\r\n");
            write("MAGENTA: " + Color.MAGENTA + "This is an example of the color." + Color.RESET + "\r\n");
            write("RED: " + Color.RED + "This is an example of the color." + Color.RESET + "\r\n");
            write("WHITE: " + Color.WHITE + "This is an example of the color." + Color.RESET + "\r\n");
            write("YELLOW: " + Color.YELLOW + "This is an example of the color." + Color.RESET + "\r\n");
            write("\r\n\r\nBOLD COLORS\r\n");
            write("BLACK: " + Color.BOLD_ON + Color.BLACK + "This is an example of the color." + Color.RESET + "\r\n");
            write("BLUE: " + Color.BOLD_ON + Color.BLUE + "This is an example of the color." + Color.RESET + "\r\n");
            write("CYAN: " + Color.BOLD_ON + Color.CYAN + "This is an example of the color." + Color.RESET + "\r\n");
            write("GREEN: " + Color.BOLD_ON + Color.GREEN + "This is an example of the color." + Color.RESET + "\r\n");
            write("MAGENTA: " + Color.BOLD_ON + Color.MAGENTA + "This is an example of the color." + Color.RESET + "\r\n");
            write("RED: " + Color.BOLD_ON + Color.RED + "This is an example of the color." + Color.RESET + "\r\n");
            write("WHITE: " + Color.BOLD_ON + Color.WHITE + "This is an example of the color." + Color.RESET + "\r\n");
            write("YELLOW: " + Color.BOLD_ON + Color.YELLOW + "This is an example of the color." + Color.RESET + "\r\n");
        });
    }
}
