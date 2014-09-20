package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class WhoCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("who");
    final static String description = "Drop an item";

    public WhoCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            Table t = new Table(3, BorderStyle.CLASSIC_COMPATIBLE,
                    ShownBorders.HEADER_AND_FIRST_COLLUMN);

            t.setColumnWidth(0, 8, 14);
            t.setColumnWidth(1, 14, 16);
            t.setColumnWidth(2, 26, 26);
            t.addCell("player");
            t.addCell("ip address");
            t.addCell("logged in since");
            Set<Player> allPlayers = gameManager.getAllPlayers();
            for (Player allPlayer : allPlayers) {
                t.addCell(allPlayer.getPlayerName());
                t.addCell(allPlayer.getChannel().getRemoteAddress().toString().substring(1).split(":")[0]);
                t.addCell(extractCreeperSession(e.getChannel()).getPrettyDate());
            }
            write(t.render());
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    public static void main(String[] args) {
        CellStyle numberStyle = new CellStyle(CellStyle.HorizontalAlign.right);

        Table t = new Table(3, BorderStyle.UNICODE_BOX,
                ShownBorders.HEADER_ONLY);

        t.setColumnWidth(0, 22, 28);
        t.setColumnWidth(1, 15, 22);
        t.setColumnWidth(2, 9, 16);

        t.addCell("Chrisadfasdfas");
        t.addCell("127.0.0.1");
        t.addCell("November 1, 1997");

        t.addCell("Brian");
        t.addCell("127.0.0.1");
        t.addCell("November 1, 1997");

        t.addCell("Turd");
        t.addCell("127.0.0.1");
        t.addCell("November 1, 1997");


        System.out.println("\n\n\n\n" + t.render());
    }

}
