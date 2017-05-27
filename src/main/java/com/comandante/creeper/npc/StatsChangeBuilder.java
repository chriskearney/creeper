package com.comandante.creeper.npc;

import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;

import java.util.List;

public class StatsChangeBuilder {
    private Stats stats;
    private List<String> damageStrings;
    private Player player;
    private Stats playerStatsChange;
    private List<String> playerDamageStrings;
    private boolean isItemDamage;

    public StatsChangeBuilder setStats(Stats stats) {
        this.stats = stats;
        return this;
    }

    public StatsChangeBuilder setDamageStrings(List<String> damageStrings) {
        this.damageStrings = damageStrings;
        return this;
    }

    public StatsChangeBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public StatsChangeBuilder setPlayerStatsChange(Stats playerStatsChange) {
        this.playerStatsChange = playerStatsChange;
        return this;
    }

    public StatsChangeBuilder setPlayerDamageStrings(List<String> playerDamageStrings) {
        this.playerDamageStrings = playerDamageStrings;
        return this;
    }

    public StatsChangeBuilder setIsItemDamage(boolean isItemDamage) {
        this.isItemDamage = isItemDamage;
        return this;
    }

    public StatsChange createNpcStatsChange() {
        return new StatsChange(stats, damageStrings, player, playerStatsChange, playerDamageStrings, isItemDamage);
    }
}