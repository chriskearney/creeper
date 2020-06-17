package com.comandante.creeper.spawner;

import com.comandante.creeper.world.model.Area;

public class SpawnRule {

    private Area area;
    private int randomChance;
    private int spawnIntervalTicks;
    private int maxInstances;
    private int maxPerRoom;

    public SpawnRule(Area area, int spawnIntervalTicks, int maxInstances, int maxPerRoom, int randomPercent) {
        this.area = area;
        this.spawnIntervalTicks = spawnIntervalTicks;
        this.maxInstances = maxInstances;
        this.maxPerRoom = maxPerRoom;
        this.randomChance = randomPercent;
    }

    public SpawnRule() {
    }

    public int getRandomChance() {
        return randomChance;
    }

    public int getMaxPerRoom() {
        return maxPerRoom;
    }

    public int getSpawnIntervalTicks() {
        return spawnIntervalTicks;
    }

    public int getMaxInstances() {
        return maxInstances;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public void setRandomChance(int randomChance) {
        this.randomChance = randomChance;
    }

    public void setSpawnIntervalTicks(int spawnIntervalTicks) {
        this.spawnIntervalTicks = spawnIntervalTicks;
    }

    public void setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
    }

    public void setMaxPerRoom(int maxPerRoom) {
        this.maxPerRoom = maxPerRoom;
    }
}

