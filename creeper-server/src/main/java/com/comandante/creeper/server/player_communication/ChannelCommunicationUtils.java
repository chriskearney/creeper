package com.comandante.creeper.server.player_communication;

public interface ChannelCommunicationUtils {

        void write(String playerId, String message);

        void write(String playerId, String message, boolean leadingBlankLine);

}
