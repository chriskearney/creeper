package com.comandante.creeper.command.commands;


import com.comandante.creeper.configuration.ConfigureCommands;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class HelpCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("help", "h");
    final static String description = "The help command.";
    final static String correctUsage = "help";


    public HelpCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            StringBuilder sb = new StringBuilder();
            Table t = new Table(2, BorderStyle.CLASSIC_COMPATIBLE,
                    ShownBorders.HEADER_FIRST_AND_LAST_COLLUMN);
            t.setColumnWidth(0, 10, 30);
            t.setColumnWidth(1, 30, 55);
            t.addCell("commands");
            t.addCell("description");
            Set<Command> creeperCommands = ConfigureCommands.creeperCommandRegistry.getCreeperCommands();
            for (Command command : creeperCommands) {
                Joiner.on(" ").join(validTriggers);
                if (command.roles != null) {
                    if (command.roles.contains(PlayerRole.ADMIN)) {
                        if (!playerManager.hasRole(player, PlayerRole.ADMIN)) {
                            continue;
                        }
                    }
                }
                t.addCell(command.correctUsage);
                t.addCell(command.description);
            }
            sb.append(t.render());
            sb.append("\r\n");
            write(sb.toString());
        });
    }
}
