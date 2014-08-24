package com.comandante.creeper.managers;


import com.comandante.creeper.model.PlayerMetadataSerializer;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import com.comandante.creeper.model.Room;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManagerMapDB implements PlayerManager {

    private ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>();
    private HTreeMap<String, PlayerMetadata> playerMetadataStore;
    private final DB db;

    public PlayerManagerMapDB(DB db) {
        this.db = db;
        if (db.exists("playerMetadata")) {
            this.playerMetadataStore = db.get("playerMetadata");
        } else {
            this.playerMetadataStore = db.createHashMap("playerMetadata").valueSerializer(new PlayerMetadataSerializer()).make();
        }
    }

    @Override
    public Set<Player> getPresentPlayers(Room room) {
        Set<String> presentPlayerIds = room.getPresentPlayerIds();
        Set<Player> players = Sets.newHashSet();
        for (String playerId: presentPlayerIds) {
            players.add(getPlayer(playerId));
        }
        return ImmutableSet.copyOf(players);
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

    @Override
    public PlayerMetadata getPlayerMetadata(String playerId) {
        return playerMetadataStore.get(playerId);
    }

    @Override
    public void savePlayerMetadata(PlayerMetadata playerMetadata) {
        playerMetadataStore.put(playerMetadata.getPlayerId(), playerMetadata);
        db.commit();
    }

    @Override
    public Player addPlayer(Player player) {
        return players.putIfAbsent(player.getPlayerId(), player);
    }

    @Override
    public Player getPlayerByUsername(String username) {
        return getPlayer(new String(Base64.encodeBase64(username.getBytes())));
    }

    @Override
    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    @Override
    public Iterator<java.util.Map.Entry<String, Player>> getPlayers() {
        return players.entrySet().iterator();
    }

    @Override
    public void removePlayer(String username) {
        Player player = getPlayerByUsername(username);
        if (player.getChannel() != null && player.getChannel().isConnected()) {
            player.getChannel().disconnect();
        }
        players.remove(player.getPlayerId());
    }

    @Override
    public boolean doesPlayerExist(String username) {
        return players.containsKey(new String(Base64.encodeBase64(username.getBytes())));
    }

}
