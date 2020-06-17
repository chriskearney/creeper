package com.comandante.creeper.items;


import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.world.model.Area;

import java.util.Set;

public class Forage extends CreeperEntity {

    private final String internalItemName;
    private final int minLevel;
    private final double pctOfSuccess;
    private final int minAmt;
    private final int maxAmt;
    private final int forageExperience;
    private final int coolDownTicks;
    private int coolDownTicksLeft;
    private final Set<Area> forageAreas;

    protected Forage(String internalItemName, int minLevel, double pctOfSuccess, int minAmt, int maxAmt, int forageExperience, int coolDownTicks, Set<Area> forageAreas) {
        this.internalItemName = internalItemName;
        this.minLevel = minLevel;
        this.pctOfSuccess = pctOfSuccess;
        this.minAmt = minAmt;
        this.maxAmt = maxAmt;
        this.coolDownTicksLeft = 0;
        this.forageExperience = forageExperience;
        this.coolDownTicks = coolDownTicks;
        this.forageAreas = forageAreas;
    }

    public Set<Area> getForageAreas() {
        return forageAreas;
    }

    public String getInternalItemName() {
        return internalItemName;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public double getPctOfSuccess() {
        return pctOfSuccess;
    }

    public int getMinAmt() {
        return minAmt;
    }

    public int getMaxAmt() {
        return maxAmt;
    }

    public int getCoolDownTicks() {
        return coolDownTicks;
    }

    public int getForageExperience() {
        return forageExperience;
    }

    public int getCoolDownTicksLeft() {
        return coolDownTicksLeft;
    }

    public void setCoolDownTicksLeft(int coolDownTicksLeft) {
        this.coolDownTicksLeft = coolDownTicksLeft;
    }

    @Override
    public void run() {
        if (coolDownTicksLeft > 0) {
            coolDownTicksLeft--;
        }
    }
}
