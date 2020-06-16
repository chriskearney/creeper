package com.comandante.creeper.npc;

import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;

import java.util.List;

public class NpcStatsChange {

    private final Stats stats;
    private final List<String> damageStrings;
    private final List<String> playerDamageStrings;
    private final Player player;
    private final Stats playerStatsChange;
    private boolean isItemDamage;

    public NpcStatsChange(Stats stats, List<String> damageStrings, Player player, Stats playerStatsChange, List<String> playerDamageStrings, boolean isItemDamage) {
        this.stats = stats;
        this.damageStrings = damageStrings;
        this.player = player;
        this.playerStatsChange = playerStatsChange;
        this.playerDamageStrings = playerDamageStrings;
        this.isItemDamage = isItemDamage;
    }

    public Stats getStats() {
        return stats;
    }

    public List<String> getDamageStrings() {
        return damageStrings;
    }

    public Player getPlayer() {
        return player;
    }

    public Stats getPlayerStatsChange() {
        return playerStatsChange;
    }

    public List<String> getPlayerDamageStrings() {
        return playerDamageStrings;
    }

    public boolean isItemDamage() {
        return isItemDamage;
    }
}
