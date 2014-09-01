package com.comandante.creeper.spawner;

public class SpawnRule {

    private int randomChance;
    private final int spawnIntervalTicks;
    private final int maxInstances;
    private final int maxPerRoom;

    public SpawnRule(int spawnIntervalTicks, int maxInstances, int maxPerRoom, int randomPercent) {
        this.spawnIntervalTicks = spawnIntervalTicks;
        this.maxInstances = maxInstances;
        this.maxPerRoom = maxPerRoom;
        this.randomChance = randomPercent;
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
}

