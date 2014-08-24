package com.comandante.creeper.managers;

import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import com.comandante.creeper.model.Room;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface PlayerManager {

    public void addInventoryId(String playerId, String inventoryId);

    public Set<Player> getPresentPlayers(Room room);

    PlayerMetadata getPlayerMetadata(String playerId);

    void savePlayerMetadata(PlayerMetadata playerMetadata);

    Player addPlayer(Player player);

    Player getPlayerByUsername(String username);

    Player getPlayer(String playerId);

    Iterator<Map.Entry<String, Player>> getPlayers();

    void removePlayer(String username);

    boolean doesPlayerExist(String username);
}
