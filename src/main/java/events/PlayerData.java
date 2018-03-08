package events;

import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.world.model.Area;

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

    public PlayerData(PlayerMetadata playerMetadata, Long level, Long xpToNextLevel, Boolean isInFight, Stats playerStatsWithLevel, Integer currentRoomId, Set<Area> currentAreas, String lookSelf) {
        this.playerMetadata = playerMetadata;
        this.level = level;
        this.xpToNextLevel = xpToNextLevel;
        this.isInFight = isInFight;
        this.playerStatsWithLevel = playerStatsWithLevel;
        this.currentRoomId = currentRoomId;
        this.currentAreas = currentAreas;
        this.lookSelf = lookSelf;
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
}
