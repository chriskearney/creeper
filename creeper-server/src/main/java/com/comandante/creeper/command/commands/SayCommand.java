package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.comandante.creeper.server.player_communication.Color.RED;
import static com.comandante.creeper.server.player_communication.Color.RESET;

public class SayCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("say");
    final static String description = "Say something to the current room.";
    final static String correctUsage = "say <message>";

    public SayCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            originalMessageParts.remove(0);
            String message = Joiner.on(" ").join(originalMessageParts);
            Set<Player> presentPlayers = currentRoom.getPresentPlayers();
            for (Player presentPlayer : presentPlayers) {
                StringBuilder sb = new StringBuilder();
                sb.append(RED);
                sb.append("<").append(player.getPlayerName()).append("> ").append(message);
                sb.append(RESET);
                if (presentPlayer.getPlayerId().equals(playerId)) {
                    write(sb.toString());
                } else {
                    channelUtils.write(presentPlayer.getPlayerId(), sb.append("\r\n").toString(), true);
                }
            }
            if (gameManager.getCreeperConfiguration().isIrcEnabled() && (Objects.equals(gameManager.getCreeperConfiguration().getIrcBridgeroomId(), currentRoom.getRoomId()))) {
                if (gameManager.getIrcBotService().getBot().isConnected()) {
                    gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(player.getPlayerName() + ": " + message);
                }
            }
        });
    }
}
