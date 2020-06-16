package com.comandante.creeper.npc;

import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;

import java.util.List;

public class NpcStatsChangeBuilder {
    private Stats stats;
    private List<String> damageStrings;
    private Player player;
    private Stats playerStatsChange;
    private List<String> playerDamageStrings;
    private boolean isItemDamage;

    public NpcStatsChangeBuilder setStats(Stats stats) {
        this.stats = stats;
        return this;
    }

    public NpcStatsChangeBuilder setDamageStrings(List<String> damageStrings) {
        this.damageStrings = damageStrings;
        return this;
    }

    public NpcStatsChangeBuilder setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public NpcStatsChangeBuilder setPlayerStatsChange(Stats playerStatsChange) {
        this.playerStatsChange = playerStatsChange;
        return this;
    }

    public NpcStatsChangeBuilder setPlayerDamageStrings(List<String> playerDamageStrings) {
        this.playerDamageStrings = playerDamageStrings;
        return this;
    }

    public NpcStatsChangeBuilder setIsItemDamage(boolean isItemDamage) {
        this.isItemDamage = isItemDamage;
        return this;
    }

    public NpcStatsChange createNpcStatsChange() {
        return new NpcStatsChange(stats, damageStrings, player, playerStatsChange, playerDamageStrings, isItemDamage);
    }
}