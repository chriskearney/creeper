package com.comandante.creeper.command;

import com.comandante.creeper.command.Command;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class UsersCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("users");
    final static String description = "Display extended inforation about who is logged in.";
    final static String correctUsage = "users";

    public UsersCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            Table t = new Table(4, BorderStyle.BLANKS,
                    ShownBorders.NONE);
            t.setColumnWidth(0, 14, 24);
            t.setColumnWidth(1, 18, 18);
            t.setColumnWidth(2, 21, 21);
            t.addCell("Player");
            t.addCell("IP");
            t.addCell("Logged in since");
            t.addCell("Idle");
            Set<Player> allPlayers = gameManager.getAllPlayers();
            for (Player allPlayer : allPlayers) {
                t.addCell(allPlayer.getPlayerName());

                InetSocketAddress remoteAddress = (InetSocketAddress) allPlayer.getChannel().getRemoteAddress();
                String remoteUsersHost = remoteAddress.getHostString();
                t.addCell(remoteUsersHost);

                DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String loginTime = format.format(new Date(playerManager.getSessionManager().getSession(allPlayer.getPlayerId()).getInitialLoginTime()));
                t.addCell(loginTime);

                long lastActivity = playerManager.getSessionManager().getSession(allPlayer.getPlayerId()).getLastActivity();
                long idleTimeMs = System.currentTimeMillis() - lastActivity;
                String idleTime = String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(idleTimeMs),
                        TimeUnit.MILLISECONDS.toSeconds(idleTimeMs) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(idleTimeMs))
                );
                t.addCell(idleTime);
            }
            write(t.render());
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
