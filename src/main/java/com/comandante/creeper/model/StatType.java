package com.comandante.creeper.model;

public enum StatType {
    STRENGTH(1),
    WILLPOWER(2),
    ENDURANCE(3),
    AIM(4),
    AGILE(5);

    private final int statTypeId;

    StatType(int statTypeId) {
        this.statTypeId = statTypeId;
    }

    public int getStatTypeId() {
        return statTypeId;
    }
}
