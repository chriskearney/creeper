package com.comandante.creeper.player.combat_simuation;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chrisk on 5/6/17.
 */
public class CombatSimulationResult {

    float playerWinPercent;
    float npcWinPercent;
    int averageRounds;
    int npcExperience;
    int averageGoldPerWin;
    Map<String, AtomicInteger> drops;

    public CombatSimulationResult(float playerWinPercent, float npcWinPercent, int averageRounds, int npcExperience, int averageGoldPerWin, Map<String, AtomicInteger> drops) {
        this.playerWinPercent = playerWinPercent;
        this.npcWinPercent = npcWinPercent;
        this.averageRounds = averageRounds;
        this.npcExperience = npcExperience;
        this.averageGoldPerWin = averageGoldPerWin;
        this.drops = drops;
    }

    public float getPlayerWinPercent() {
        return playerWinPercent;
    }

    public float getNpcWinPercent() {
        return npcWinPercent;
    }

    public int getAverageRounds() {
        return averageRounds;
    }

    public int getNpcExperience() {
        return npcExperience;
    }

    public Map<String, AtomicInteger> getDrops() {
        return drops;
    }

    public int getAverageGoldPerWin() {
        return averageGoldPerWin;
    }
}
