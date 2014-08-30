package com.comandante.creeper.fight;

public class FightResults {

    private final boolean npcWon;
    private final boolean playerWon;

    public FightResults(boolean npcWon, boolean playerWon) {
        this.npcWon = npcWon;
        this.playerWon = playerWon;
    }
}
