package com.comandante.creeper.player;


import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.npc.BasicNpcLevelStatsModifier;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.stat.Stats;

public class StatsModifierFactory {

    private final GameManager gameManager;

    public StatsModifierFactory(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public Stats getStatsModifier(Player player) {
        BasicPlayerLevelStatsModifier basicPlayerLevelStatsModifier = new BasicPlayerLevelStatsModifier(gameManager);
        return basicPlayerLevelStatsModifier.modify(player);
    }

    public Stats getStatsModifier(Npc npc) {
        BasicNpcLevelStatsModifier basicNpcLevelStatsModifier = new BasicNpcLevelStatsModifier(gameManager);
        return basicNpcLevelStatsModifier.modify(npc);
    }
}
