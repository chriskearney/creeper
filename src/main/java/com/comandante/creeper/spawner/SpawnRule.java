package com.comandante.creeper.spawner;

import com.google.common.base.Optional;

public class SpawnRule {

    private Optional<Integer> randomChance = Optional.absent();
    private final int spawnIntervalTicks;
    private final int maxPerRoom;

    public SpawnRule(int spawnIntervalTicks, int maxPerRoom) {
        this.spawnIntervalTicks = spawnIntervalTicks;
        this.maxPerRoom = maxPerRoom;
    }

    public SpawnRule(int spawnIntervalTicks, int maxPerRoom, int randomPercent) {
        this.spawnIntervalTicks = spawnIntervalTicks;
        this.maxPerRoom = maxPerRoom;
        this.randomChance = Optional.of(randomPercent);
    }

    public Optional<Integer> getRandomChance() {
        return randomChance;
    }

    public int getSpawnIntervalTicks() {
        return spawnIntervalTicks;
    }

    public int getMaxPerRoom() {
        return maxPerRoom;
    }
}

