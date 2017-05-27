package com.comandante.creeper.npc;

import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;

import java.util.List;

public class StatsChange {

    private final Stats targetStatsChange;
    private final List<String> damageStrings;
    private final List<String> playerDamageStrings;
    private final Player sourcePlayer;
    private final Stats sourcePlayerStatsChange;
    private boolean isItemDamage;

    public StatsChange(Stats targetStatsChange, List<String> damageStrings, Player sourcePlayer, Stats sourcePlayerStatsChange, List<String> playerDamageStrings, boolean isItemDamage) {
        this.targetStatsChange = targetStatsChange;
        this.damageStrings = damageStrings;
        this.sourcePlayer = sourcePlayer;
        this.sourcePlayerStatsChange = sourcePlayerStatsChange;
        this.playerDamageStrings = playerDamageStrings;
        this.isItemDamage = isItemDamage;
    }

    public Stats getTargetStatsChange() {
        return targetStatsChange;
    }

    public List<String> getDamageStrings() {
        return damageStrings;
    }

    public Player getSourcePlayer() {
        return sourcePlayer;
    }

    public Stats getSourcePlayerStatsChange() {
        return sourcePlayerStatsChange;
    }

    public List<String> getPlayerDamageStrings() {
        return playerDamageStrings;
    }

    public boolean isItemDamage() {
        return isItemDamage;
    }
}
