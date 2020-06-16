package com.comandante.creeper.server.player_communication;

import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.world.RoomManager;

public class ChannelUtils implements ChannelCommunicationUtils {

    private final PlayerManager playerManager;
    private final RoomManager roomManager;

    public ChannelUtils(PlayerManager playerManager, RoomManager roomManager) {
        this.playerManager = playerManager;
        this.roomManager = roomManager;
    }

    public void write(String playerId, String message) {
        write(playerId, message, false);
    }

    public void write(String playerId, String message, boolean leadingBlankLine) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        Player player = playerManager.getPlayer(playerId);
        String lastMessage = playerManager.getSessionManager().getSession(playerId).getLastMessage();
        StringBuilder sb = new StringBuilder();
        if (lastMessage != null && (lastMessage.length() >= "\r\n".length())) {
            if (!lastMessage.substring(lastMessage.length() - 2).equals("\r\n")) {
                if (leadingBlankLine) {
                    sb.append("\r\n");
                }
            }
        }
        sb.append(message);
        playerManager.getSessionManager().getSession(playerId).setLastMessage(sb.toString());
        player.getChannel().write(sb.toString());
    }
}

