package com.comandante.creeper.model;

import java.io.Serializable;

public class Stats implements Serializable {
    float strength;
    float willpower;
    float endurance;
    float aim;
    float agile;

    public Stats(float strength, float willpower, float endurance, float aim, float agile) {
        this.strength = strength;
        this.willpower = willpower;
        this.endurance = endurance;
        this.aim = aim;
        this.agile = agile;
    }

    public float getStrength() {
        return strength;
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    public float getWillpower() {
        return willpower;
    }

    public void setWillpower(float willpower) {
        this.willpower = willpower;
    }

    public float getEndurance() {
        return endurance;
    }

    public void setEndurance(float endurance) {
        this.endurance = endurance;
    }

    public float getAim() {
        return aim;
    }

    public void setAim(float aim) {
        this.aim = aim;
    }

    public float getAgile() {
        return agile;
    }

    public void setAgile(float agile) {
        this.agile = agile;
    }
}
