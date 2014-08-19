package com.comandante.creeper.managers;


import com.comandante.creeper.model.Player;
import org.apache.commons.codec.binary.Base64;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>();

    public Player addPlayer(Player player) {
        return players.putIfAbsent(player.getPlayerId(), player);
    }

    public Player getPlayerByUsername(String username) {
        return getPlayer(new String(Base64.encodeBase64(username.getBytes())));
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public Iterator<java.util.Map.Entry<String, Player>> getPlayers() {
        return players.entrySet().iterator();
    }

    public void removePlayer(String username) {
        Player player = getPlayerByUsername(username);
        if (player.getChannel() != null) {
            player.getChannel().disconnect();
        }
        players.remove(player.getPlayerId());
    }

    public boolean doesPlayerExist(String username) {
       return players.containsKey(new String(Base64.encodeBase64(username.getBytes())));
    }
}
