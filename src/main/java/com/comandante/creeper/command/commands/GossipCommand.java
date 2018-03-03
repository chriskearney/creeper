package com.comandante.creeper.command.commands;


import com.comandante.creeper.Creeper;
import com.comandante.creeper.bot.command.commands.BotCommand;
import com.comandante.creeper.core_game.GameManager;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import events.CreeperEvent;
import events.CreeperEventType;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.comandante.creeper.server.player_communication.Color.*;

public class GossipCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("gossip", "g");
    final static String description = "Sends a message to the entire MUD.";
    final static String correctUsage = "gossip <message>";

    public GossipCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (!player.isChatModeOn()) {
                if (originalMessageParts.size() == 1) {
                    write("Nothing to gossip about?");
                    return;
                }
                originalMessageParts.remove(0);
            }
            String msg = Joiner.on(" ").join(originalMessageParts);
            try {
                if (msg.startsWith("!!")) {
                    String botCommandOutput = getBotCommandOutput(msg);
                    msg = msg + "\r\n" + botCommandOutput;
                }
            } catch (Exception ex) {
                log.error("Problem executing bot command from gossip channel!", ex);
            }
            String gossipMessage = WHITE + "[" + RESET + MAGENTA + this.player.getPlayerName() + WHITE + "] " + RESET + CYAN + msg + RESET;

            CreeperEvent build = new CreeperEvent.Builder()
                    .playerId(playerId)
                    .payload(gossipMessage)
                    .epochTimestamp(System.currentTimeMillis())
                    .creeperEventType(CreeperEventType.GOSSIP)
                    .audience(CreeperEvent.Audience.EVERYONE)
                    .build();

            gameManager.getListenerService().post(build);


            playerManager.getAllPlayersMap().forEach((s, destinationPlayer) -> {
                if (destinationPlayer.getPlayerId().equals(playerId)) {
                    write(gossipMessage);
                } else {
                    channelUtils.write(destinationPlayer.getPlayerId(), gossipMessage + "\r\n", true);
                }
            });
            gameManager.getGossipCache().addGossipLine(gossipMessage);
        });
    }

    private String getBotCommandOutput(String cmd) {
        ArrayList<String> originalMessageParts = Lists.newArrayList(Arrays.asList(cmd.split("!!")));
        originalMessageParts.remove(0);
        final String msg = Joiner.on(" ").join(originalMessageParts);
        BotCommand command = gameManager.getBotCommandFactory().getCommand(null, msg);
        if (command != null) {
            List<String> process = command.process();
            StringBuilder sb = new StringBuilder();
            for (String line : process) {
                sb.append(line).append("\r\n");
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}
