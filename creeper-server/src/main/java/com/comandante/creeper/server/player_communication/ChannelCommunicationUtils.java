package com.comandante.creeper.server.player_communication;

import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.world.RoomManager;

public interface ChannelCommunicationUtils {

        void write(String playerId, String message);

        void write(String playerId, String message, boolean leadingBlankLine);

}
