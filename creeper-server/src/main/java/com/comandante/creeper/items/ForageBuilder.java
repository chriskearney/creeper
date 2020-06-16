package com.comandante.creeper.items;

import com.comandante.creeper.world.model.Area;

import java.util.Set;

public class ForageBuilder {
    private String internalItemName;
    private int minLevel;
    private double pctOfSuccess;
    private int minAmt;
    private int maxAmt;
    private int forageExperience;
    private int coolDownTicks;
    private Set<Area> forageAreas;


    public ForageBuilder from(Forage forage) {
        this.internalItemName = forage.getInternalItemName();
        this.minLevel = new Integer(forage.getMinLevel());
        this.pctOfSuccess = new Double(forage.getPctOfSuccess());
        this.minAmt = new Integer(forage.getMinAmt());
        this.maxAmt = new Integer(forage.getMaxAmt());
        this.coolDownTicks = new Integer(forage.getCoolDownTicks());
        //this.coolDownTicksLeft = new Integer(0);
        this.forageExperience = new Integer(forage.getForageExperience());
        this.forageAreas = forage.getForageAreas();
        return this;
    }

    public ForageBuilder setAreas(Set<Area> forageAreas) {
        this.forageAreas = forageAreas;
        return this;
    }

    public ForageBuilder setInternalItemName(String internalItemName) {
        this.internalItemName = internalItemName;
        return this;
    }

    public ForageBuilder setMinLevel(int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    public ForageBuilder setPctOfSuccess(double pctOfSuccess) {
        this.pctOfSuccess = pctOfSuccess;
        return this;
    }

    public ForageBuilder setMinAmt(int minAmt) {
        this.minAmt = minAmt;
        return this;
    }

    public ForageBuilder setMaxAmt(int maxAmt) {
        this.maxAmt = maxAmt;
        return this;
    }

    public ForageBuilder setForageExperience(int forageExperience) {
        this.forageExperience = forageExperience;
        return this;
    }

    public ForageBuilder setCoolDownTicks(int coolDownTicks) {
        this.coolDownTicks = coolDownTicks;
        return this;
    }

    public Forage createForage() {
        return new Forage(internalItemName, minLevel, pctOfSuccess, minAmt, maxAmt, forageExperience, coolDownTicks, forageAreas);
    }
}