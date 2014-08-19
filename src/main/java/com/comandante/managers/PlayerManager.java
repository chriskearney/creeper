package com.comandante.managers;


import com.comandante.model.Player;
import org.apache.commons.codec.binary.Base64;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>();

    public Player addPlayer(Player player) {
        return players.putIfAbsent(player.getPlayerId(), player);
    }

    public Player getPlayer(Player player) {
        return players.get(player.getPlayerId());
    }

    public Player getPlayer(String username) {
        return players.get(new String(Base64.encodeBase64(username.getBytes())));
    }

    public Iterator<java.util.Map.Entry<String, Player>> getPlayers() {
        return players.entrySet().iterator();
    }

    public void removePlayer(String username) {
        Player player = getPlayer(username);
        if (player.getChannel() != null) {
            player.getChannel().disconnect();
        }
        players.remove(player);
    }

    public boolean doesPlayerExist(String username) {
       return players.containsKey(Base64.encodeBase64(username.getBytes()));
    }
}
