package events;

import com.comandante.creeper.items.Item;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.world.model.Area;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerData {

    private final PlayerMetadata playerMetadata;
    private final Long level;
    private final Long xpToNextLevel;
    private final Boolean isInFight;
    private final Stats playerStatsWithLevel;
    private final Integer currentRoomId;
    private final Set<Area> currentAreas;
    private final String lookSelf;
    private final List<String> inventory;
    private final Map<String, Item> itemMap;
    private final Map<String, String> presentPlayers;
    private final Set<Item> presentItems;
    private final Map<String, String> npcs;

    public PlayerData(PlayerMetadata playerMetadata,
                      Long level,
                      Long xpToNextLevel,
                      Boolean isInFight,
                      Stats playerStatsWithLevel,
                      Integer currentRoomId,
                      Set<Area> currentAreas,
                      String lookSelf,
                      List<String> inventory,
                      Map<String, Item> itemMap,
                      Map<String, String> presentPlayers,
                      Set<Item> presentItems,
                      Map<String, String> npcs) {
        this.playerMetadata = playerMetadata;
        this.level = level;
        this.xpToNextLevel = xpToNextLevel;
        this.isInFight = isInFight;
        this.playerStatsWithLevel = playerStatsWithLevel;
        this.currentRoomId = currentRoomId;
        this.currentAreas = currentAreas;
        this.lookSelf = lookSelf;
        this.inventory = inventory;
        this.itemMap = itemMap;
        this.presentPlayers = presentPlayers;
        this.presentItems = presentItems;
        this.npcs = npcs;
    }

    public PlayerMetadata getPlayerMetadata() {
        return playerMetadata;
    }

    public Long getXpToNextLevel() {
        return xpToNextLevel;
    }

    public Boolean getInFight() {
        return isInFight;
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

    public Map<String, String> getNpcs() {
        return npcs;
    }
}