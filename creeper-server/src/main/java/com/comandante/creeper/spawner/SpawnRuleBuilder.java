package com.comandante.creeper.spawner;
import com.comandante.creeper.world.model.Area;

public class SpawnRuleBuilder {
    private Area area;
    private int spawnIntervalTicks;
    private int maxInstances;
    private int maxPerRoom;
    private int randomPercent;

    public SpawnRuleBuilder setArea(Area area) {
        this.area = area;
        return this;
    }

    public SpawnRuleBuilder setSpawnIntervalTicks(int spawnIntervalTicks) {
        this.spawnIntervalTicks = spawnIntervalTicks;
        return this;
    }

    public SpawnRuleBuilder setMaxInstances(int maxInstances) {
        this.maxInstances = maxInstances;
        return this;
    }

    public SpawnRuleBuilder setMaxPerRoom(int maxPerRoom) {
        this.maxPerRoom = maxPerRoom;
        return this;
    }

    public SpawnRuleBuilder setRandomPercent(int randomPercent) {
        this.randomPercent = randomPercent;
        return this;
    }


    public SpawnRule createSpawnRule() {
        return new SpawnRule(area, spawnIntervalTicks, maxInstances, maxPerRoom, randomPercent);
    }
}