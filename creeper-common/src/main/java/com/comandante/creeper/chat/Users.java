package com.comandante.creeper.chat;

import java.util.Map;

public class Users {

    private Map<String, String> userMap;
    private String playerId;

    public Users(Map<String, String> userMap, String playerId) {
        this.userMap = userMap;
        this.playerId = playerId;
    }

    public Users() {
    }

    public Map<String, String> getUserMap() {
        return userMap;
    }

    public String getPlayerId() {
        return playerId;
    }
}
