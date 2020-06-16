package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.server.player_communication.Color;
import com.google.common.collect.Lists;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CountdownCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("countdown", "??");
    final static String description = "a countdown.";
    final static String correctUsage = "?? countdown";

    public CountdownCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommandBackgroundThread(ctx, e, () -> {
            ArrayList<String> countDownMessages =
                    Lists.newArrayList("... ***** COUNTDOWN ***** ...",
                            ".             5             .",
                            ".             4             .",
                            ".             3             .",
                            ".             2             .",
                            ".             1             .",
                            "... *****   SMOKE!  ***** ...");


            countDownMessages.forEach(message -> {
                writeMessageToEveryPlayer(message);
                try {
                    Thread.sleep(900);
                } catch (InterruptedException ex) {
                    log.error("Problem while printing countdown message", ex);
                }
            });
        });
    }

    private void writeMessageToEveryPlayer(String message) {
        playerManager.getAllPlayersMap().forEach((playerId1, player1) -> channelUtils.write(playerId1, Color.BOLD_ON + Color.GREEN + message + Color.RESET + "\r\n", true));
    }
}
