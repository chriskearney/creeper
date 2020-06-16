package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KillTallyCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("killtally", "kt", "tally");
    final static String description = "View your kill tally.";
    final static String correctUsage = "tally";

    public KillTallyCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        this.execCommand(ctx, e, () -> {
            Table table = new Table(2, BorderStyle.CLASSIC_COMPATIBLE, ShownBorders.HEADER_ONLY);
            table.setColumnWidth(0, 22, 30);
            table.setColumnWidth(1, 10, 20);
            table.addCell("Npc");
            table.addCell("# Killed");
            Map<String, Long> npcKillLog = player.getNpcKillLog();
            npcKillLog.forEach((s, aLong) -> {
                table.addCell(s);
                table.addCell(String.valueOf(aLong));
            });
            write(table.render());
        });
    }
}
