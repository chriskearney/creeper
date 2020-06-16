package com.comandante.creeper.player;


public class CoolDown {

    private int numberOfTicks;
    private final String name;
    private final CoolDownType coolDownType;
    private final int originalNumberOfTicks;

    public CoolDown(CoolDownType coolDownType) {
        this.name = coolDownType.getName();
        this.numberOfTicks = coolDownType.getTicks();
        this.coolDownType = coolDownType;
        this.originalNumberOfTicks = coolDownType.getTicks();
    }

    public CoolDown(String name, int numberOfTicks, CoolDownType coolDownType){
        this.name = name;
        this.numberOfTicks = numberOfTicks;
        this.coolDownType = coolDownType;
        this.originalNumberOfTicks = numberOfTicks;
    }

    public void decrementTick() {
        if (numberOfTicks > 0) {
            this.numberOfTicks = numberOfTicks - 1;
        }
    }

    public boolean isActive() {
        return numberOfTicks > 0;
    }

    public CoolDownType getCoolDownType() {
        return coolDownType;
    }

    public String getName() {
        return name;
    }

    public int getNumberOfTicks() {
        return numberOfTicks;
    }

    public int getOriginalNumberOfTicks() {
        return originalNumberOfTicks;
    }

    public void setNumberOfTicks(int numberOfTicks) {
        this.numberOfTicks = numberOfTicks;
    }
}
