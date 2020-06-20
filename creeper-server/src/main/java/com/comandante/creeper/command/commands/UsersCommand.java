package com.comandante.creeper.command.commands;

import com.comandante.creeper.common.FriendlyTime;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class UsersCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("users");
    final static String description = "Display extended inforation about who is logged in.";
    final static String correctUsage = "users";

    public UsersCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, null);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
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

                SimpleDateFormat loggedInFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                String loginTime = loggedInFormat.format(new Date(playerManager.getSessionManager().getSession(allPlayer.getPlayerId()).getInitialLoginTime()));
                t.addCell(loginTime);

                long lastActivity = playerManager.getSessionManager().getSession(allPlayer.getPlayerId()).getLastActivity();
                String idleTime = getFriendlyTime(new Date(lastActivity));
                t.addCell(idleTime);
            }
            write(t.render());
        });
    }


    public static String getFriendlyTime(Date dateTime) {
        Date current = Calendar.getInstance().getTime();
        long seconds = (current.getTime() - dateTime.getTime()) / 1000;

        return new FriendlyTime(seconds).getFriendlyFormattedShort();
    }
}
