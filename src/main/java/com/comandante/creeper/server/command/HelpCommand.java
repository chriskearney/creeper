package com.comandante.creeper.server.command;


import com.comandante.creeper.Main;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.world.Area;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.*;

public class HelpCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("h", "help");
    final static String description = "This help command.";

    public HelpCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            Set<Command> creeperCommands = Main.creeperCommandRegistry.getCreeperCommands();
            for (Command command : creeperCommands) {
                List<String> validTriggers1 = command.validTriggers;
                for (String s: validTriggers1) {
                    write(s + " ");
                }
                write(": " + command.description + "\r\n");
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
