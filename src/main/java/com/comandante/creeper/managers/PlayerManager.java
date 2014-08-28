package com.comandante.creeper.managers;


import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import com.comandante.creeper.model.PlayerMetadataSerializer;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.model.Stats;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>();
    private HTreeMap<String, PlayerMetadata> playerMetadataStore;
    private final DB db;

    public PlayerManager(DB db) {
        this.db = db;
        if (db.exists("playerMetadata")) {
            this.playerMetadataStore = db.get("playerMetadata");
        } else {
            this.playerMetadataStore = db.createHashMap("playerMetadata").valueSerializer(new PlayerMetadataSerializer()).make();
        }
    }

    public Set<Player> getPresentPlayers(Room room) {
        Set<String> presentPlayerIds = room.getPresentPlayerIds();
        Set<Player> players = Sets.newHashSet();
        for (String playerId : presentPlayerIds) {
            players.add(getPlayer(playerId));
        }
        return ImmutableSet.copyOf(players);
    }

    public int getNumberOfLoggedInUsers() {
        int cnt = 0;
        Iterator<Map.Entry<String, Player>> players = getPlayers();
        while (players.hasNext()) {
            Map.Entry<String, Player> next = players.next();
            cnt++;
        }
        return cnt;
    }

    public String getPrompt(String playerId, Integer roomId) {
        StringBuilder sb = new StringBuilder()
                .append("[")
                .append(getPlayer(playerId).getPlayerName())
                .append(" roomId:")
                .append(roomId)
                .append((" users:"))
                .append(getNumberOfLoggedInUsers())
                .append("] ");
        return sb.toString();
    }

    public void addInventoryId(String playerId, String inventoryId) {
        PlayerMetadata playerMetadata = playerMetadataStore.get(playerId);
        playerMetadata.addInventoryEntityId(inventoryId);
        savePlayerMetadata(playerMetadata);
    }

    public void removeInventoryId(String playerId, String inventoryId) {
        PlayerMetadata playerMetadata = playerMetadataStore.get(playerId);
        playerMetadata.removeInventoryEntityId(inventoryId);
        savePlayerMetadata(playerMetadata);
    }

    public PlayerMetadata getPlayerMetadata(String playerId) {
        return playerMetadataStore.get(playerId);
    }

    public void savePlayerMetadata(PlayerMetadata playerMetadata) {
        playerMetadataStore.put(playerMetadata.getPlayerId(), playerMetadata);
        db.commit();
    }

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
        if (player.getChannel() != null && player.getChannel().isConnected()) {
            player.getChannel().disconnect();
        }
        players.remove(player.getPlayerId());
    }

    public boolean doesPlayerExist(String username) {
        return players.containsKey(new String(Base64.encodeBase64(username.getBytes())));
    }

    public String getLookString(Player player) {
        PlayerMetadata playerMetadata = getPlayerMetadata(player.getPlayerId());
        Stats playerStats = playerMetadata.getStats();
        return playerStats.toString();
    }

}
