package com.comandante.creeper.fight;

public class FightResultsBuilder {
    private boolean npcWon;
    private boolean playerWon;

    public FightResultsBuilder setNpcWon(boolean npcWon) {
        this.npcWon = npcWon;
        return this;
    }

    public FightResultsBuilder setPlayerWon(boolean playerWon) {
        this.playerWon = playerWon;
        return this;
    }

    public FightResults createFightResults() {
        return new FightResults(npcWon, playerWon);
    }
}