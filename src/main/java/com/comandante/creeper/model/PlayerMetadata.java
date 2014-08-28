package com.comandante.creeper.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerMetadata implements Serializable {

    String playerName;
    String password;
    String playerId;
    Stats stats;
    String[] inventory;

    /*STRENGTH -
WILLPOWER - HEALTH REGENATION HOW EFFECTIVE HEALING DEVICES ARE
                - INCREASES CHANCE OF CRITICAL
ENDURANCE - INCREASE TOTAL HEALTH
        -RATE AT WHICH NATURAL TICK HEALTH REGENATION WORKS
        - RATE OF MOVEMENT
AIM - CHANCE TO HIT
        AND DAMAGE PER HIT INCREASE
AGILE - CRITICAL CHANCE INCREASE
        - FLEE INCREASE
        - DODGE/PARRY INCREASE

*/

    public PlayerMetadata(String playerName, String password, String playerId, Stats stats) {
        this.playerName = playerName;
        this.password = password;
        this.playerId = playerId;
        this.stats = stats;
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
        for (String id: itemsIdKeep) {
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
}
