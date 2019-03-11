package com.comandante.creeper.player.combat_simuation;

/**
 * Created by chrisk on 5/6/17.
 */
public class FightSimulationResult {

    private final boolean result;
    private final int totalFightRounds;

    public FightSimulationResult(boolean result, int totalFightRounds) {
        this.result = result;
        this.totalFightRounds = totalFightRounds;
    }

    public boolean isResult() {
        return result;
    }

    public int getTotalFightRounds() {
        return totalFightRounds;
    }
}
