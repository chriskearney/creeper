package com.comandante.creeper.core_game;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.server.model.CreeperSession;

import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private final ConcurrentHashMap<String, CreeperSession> sessionMap = new ConcurrentHashMap<>();

    public void putSession(CreeperSession creeperSession) {
        sessionMap.put(Creeper.createPlayerId(creeperSession.getUsername().get()), creeperSession);
    }

    public CreeperSession getSession(String playerId) {
        return sessionMap.get(playerId);
    }
}
