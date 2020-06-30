package com.comandante.creeper.events;

import com.comandante.creeper.items.Item;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.world.model.Area;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class PlayerData {

    private PlayerMetadata playerMetadata;
    private Long level;
    private Long xpToNextLevel;
    private Boolean inFight;
    private Stats playerStatsWithLevel;
    private Integer currentRoomId;
    private Set<Area> currentAreas;
    private String lookSelf;
    private List<String> inventory;
    private Map<String, Item> itemMap;
    private Map<String, String> presentPlayers;
    private Set<Item> presentItems;
    private Map<String, String> presentNpcs;
    private Map<String, String> presentMerchants;

    public PlayerData() {
    }

    public PlayerData(PlayerMetadata playerMetadata,
                      Long level,
                      Long xpToNextLevel,
                      Boolean inFight,
                      Stats playerStatsWithLevel,
                      Integer currentRoomId,
                      Set<Area> currentAreas,
                      String lookSelf,
                      List<String> inventory,
                      Map<String, Item> itemMap,
                      Map<String, String> presentPlayers,
                      Set<Item> presentItems,
                      Map<String, String> presentNpcs,
                      Map<String, String> presentMerchants) {
        this.playerMetadata = playerMetadata;
        this.level = level;
        this.xpToNextLevel = xpToNextLevel;
        this.inFight = inFight;
        this.playerStatsWithLevel = playerStatsWithLevel;
        this.currentRoomId = currentRoomId;
        this.currentAreas = currentAreas;
        this.lookSelf = lookSelf;
        this.inventory = inventory;
        this.itemMap = itemMap;
        this.presentPlayers = presentPlayers;
        this.presentItems = presentItems;
        this.presentNpcs = presentNpcs;
    }

    public PlayerMetadata getPlayerMetadata() {
        return playerMetadata;
    }

    public Long getXpToNextLevel() {
        return xpToNextLevel;
    }

    public Boolean getInFight() {
        return inFight;
    }

    public Stats getPlayerStatsWithLevel() {
        return playerStatsWithLevel;
    }

    public Long getLevel() {
        return level;
    }

    public Integer getCurrentRoomId() {
        return currentRoomId;
    }

    public Set<Area> getCurrentAreas() {
        return currentAreas;
    }

    public String getLookSelf() {
        return lookSelf;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public Map<String, Item> getItemMap() {
        return itemMap;
    }

    public Map<String, String> getPresentPlayers() {
        return presentPlayers;
    }

    public Set<Item> getPresentItems() {
        return presentItems;
    }

    public Map<String, String> getPresentNpcs() {
        return presentNpcs;
    }

    public Map<String, String> getPresentMerchants() {
        return presentMerchants;
    }
}
