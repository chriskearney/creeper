package com.comandante.creeper.model;

public class FightResults {
    private final Stats challenger;
    private final Stats victim;

    public FightResults(Stats challenger, Stats victim) {
        this.challenger = challenger;
        this.victim = victim;
    }
}
