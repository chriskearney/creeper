package com.comandante.creeper.items;

public enum LockPickingDifficulty {

    NOVICE(80.0),
    AMATEUR(60.0),
    PROFESIONAL(40.0),
    MASTER(20.0);

    private final double pctSuccess;

    LockPickingDifficulty(double pctSuccess) {
        this.pctSuccess = pctSuccess;
    }

    public double getPctSuccess() {
        return pctSuccess;
    }
}
