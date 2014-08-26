package com.comandante.creeper.Items;

import com.google.common.base.Optional;

public class ItemSpawnRule {

    private Optional<Float> randomChance = Optional.absent();
    private final int spawnIntervalTicks;
    private final int maxPerRoom;

    public ItemSpawnRule(int spawnIntervalTicks, int maxPerRoom) {
        this.spawnIntervalTicks = spawnIntervalTicks;
        this.maxPerRoom = maxPerRoom;
    }

    public Optional<Float> getRandomChance() {
        return randomChance;
    }

    public int getSpawnIntervalTicks() {
        return spawnIntervalTicks;
    }

    public int getMaxPerRoom() {
        return maxPerRoom;
    }
}

