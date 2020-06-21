package com.comandante.creeper.chat;

import static com.comandante.creeper.server.player_communication.Color.CYAN;
import static com.comandante.creeper.server.player_communication.Color.MAGENTA;
import static com.comandante.creeper.server.player_communication.Color.RESET;
import static com.comandante.creeper.server.player_communication.Color.WHITE;

public class Utils {


    public static String buildGossipString(String timestamp, String name, String message) {
        return WHITE + "[" + RESET + MAGENTA + name + WHITE + "] " + RESET + CYAN + message + RESET;
    }
}
