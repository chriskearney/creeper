package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

import static com.comandante.creeper.server.Color.RESET;
import static com.comandante.creeper.server.Color.YELLOW;

public class TellCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("t", "tell");
    final static String description = "Tell something to someone.";

    public TellCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession session = getCreeperSession(e.getChannel());
            Player sourcePlayer = getGameManager().getPlayerManager().getPlayer(getPlayerId(session));
            List<String> parts = getOriginalMessageParts(e);
            if (parts.size() < 3) {
                getGameManager().getChannelUtils().write(sourcePlayer.getPlayerId(), "tell failed, no message to send.");
                return;
            }
            //remove the literal 'tell'
            parts.remove(0);
            String destinationUsername = parts.get(0);
            Player desintationPlayer = getGameManager().getPlayerManager().getPlayerByUsername(destinationUsername);
            if (desintationPlayer == null) {
                getGameManager().getChannelUtils().write(sourcePlayer.getPlayerId(), "tell failed, unknown user.");
                return;
            }
            if (desintationPlayer.getPlayerId().equals(sourcePlayer.getPlayerId())) {
                getGameManager().getChannelUtils().write(sourcePlayer.getPlayerId(), "tell failed, you're talking to yourself.");
                return;
            }
            parts.remove(0);
            String tellMessage = Joiner.on(" ").join(parts);
            StringBuilder stringBuilder = new StringBuilder();
            String destinationPlayercolor = YELLOW;
            stringBuilder.append("*").append(sourcePlayer.getPlayerName()).append("* ");
            stringBuilder.append(tellMessage);
            stringBuilder.append(RESET);
            getGameManager().getChannelUtils().writeNoPrompt(desintationPlayer.getPlayerId(), destinationPlayercolor + stringBuilder.toString());
            getGameManager().getChannelUtils().write(sourcePlayer.getPlayerId(), stringBuilder.toString());
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
