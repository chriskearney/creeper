package com.comandante.creeper.player;


import com.comandante.creeper.stat.Stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PlayerMetadata implements Serializable {

    private String playerName;
    private String password;
    private String playerId;
    Stats stats;
    String[] inventory;
    private int gold;

    public PlayerMetadata(String playerName, String password, String playerId, Stats stats, int gold) {
        this.playerName = playerName;
        this.password = password;
        this.playerId = playerId;
        this.stats = stats;
        this.gold = gold;
    }

    public String[] getInventory() {
        return inventory;
    }

    public void setInventory(String[] inventory) {
        this.inventory = inventory;
    }

    public void addInventoryEntityId(String newEntityId) {
        if (inventory == null) {
            inventory = new String[0];
        }
        String[] result = Arrays.copyOf(inventory, inventory.length + 1);
        result[inventory.length] = newEntityId;
        this.inventory = result;
    }

    public void removeInventoryEntityId(String itemId) {
        List<String> itemsIdKeep = new ArrayList<String>(Arrays.asList(inventory));
        itemsIdKeep.remove(itemId);
        String[] newItems = new String[itemsIdKeep.size()];
        int i = 0;
        for (String id : itemsIdKeep) {
            newItems[i] = id;
            i++;
        }
        this.inventory = newItems;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Stats getStats() {
        return stats;
    }

    public int getGold() {
        return gold;
    }

    public void incrementGold(int amt) {
        this.gold = gold + amt;
    }
}
