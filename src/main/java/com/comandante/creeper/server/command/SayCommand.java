package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.comandante.creeper.server.Color.RED;
import static com.comandante.creeper.server.Color.RESET;

public class SayCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("say");
    final static String description = "Say something to the world.";

    public SayCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession session = extractCreeperSession(e.getChannel());
            List<String> originalMessageParts = getOriginalMessageParts(e);
            originalMessageParts.remove(0);
            Player sourcePlayer = getGameManager().getPlayerManager().getPlayer(getPlayerId(session));
            String message = Joiner.on(" ").join(originalMessageParts);
            Optional<Room> playerCurrentRoomOpt = getGameManager().getRoomManager().getPlayerCurrentRoom(sourcePlayer);
            if (!playerCurrentRoomOpt.isPresent()) {
                throw new RuntimeException("playerCurrentRoom is missing!");
            }
            Room playerCurrentRoom = playerCurrentRoomOpt.get();
            Set<Player> presentPlayers = getGameManager().getPlayerManager().getPresentPlayers(playerCurrentRoom);
            for (Player presentPlayer : presentPlayers) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(RED);
                stringBuilder.append("<").append(sourcePlayer.getPlayerName()).append("> ").append(message);
                stringBuilder.append(RESET);
                if (presentPlayer.getPlayerId().equals(sourcePlayer.getPlayerId())) {
                    getGameManager().getChannelUtils().write(sourcePlayer.getPlayerId(), stringBuilder.toString());
                } else {
                    getGameManager().getChannelUtils().writeNoPrompt(presentPlayer.getPlayerId(), stringBuilder.toString());
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
