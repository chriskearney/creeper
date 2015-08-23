package com.comandante.creeper.npc;

import com.comandante.creeper.Items.Loot;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.spawner.SpawnRule;
import com.comandante.creeper.stat.Stats;
import com.comandante.creeper.world.Area;

import java.util.Set;

public class NpcBuilder {
    private GameManager gameManager;
    private String name;
    private long level;
    private String colorName;
    private long lastPhraseTimestamp;
    private Stats stats;
    private String dieMessage;
    private Set<Area> roamAreas;
    private Set<String> validTriggers;
    private Loot loot;
    private Set<SpawnRule> spawnRules;

    public NpcBuilder() {
    }

    public NpcBuilder(Npc npc) {
        this.name = npc.getName();
        this.level = npc.getLevel();
        this.colorName = npc.getColorName();
        this.lastPhraseTimestamp = npc.getLastPhraseTimestamp();
        this.stats = new Stats(npc.getStats());
        this.dieMessage = npc.getDieMessage();
        this.roamAreas = npc.getRoamAreas();
        this.validTriggers = npc.getValidTriggers();
        this.loot = npc.getLoot();
        this.spawnRules = npc.getSpawnRules();
        this.gameManager = npc.getGameManager();
    }

    public NpcBuilder setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
        return this;
    }

    public NpcBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public NpcBuilder setLevel(long level) {
        this.level = level;
        return this;
    }

    public NpcBuilder setColorName(String colorName) {
        this.colorName = colorName;
        return this;
    }

    public NpcBuilder setLastPhraseTimestamp(long lastPhraseTimestamp) {
        this.lastPhraseTimestamp = lastPhraseTimestamp;
        return this;
    }

    public NpcBuilder setStats(Stats stats) {
        this.stats = stats;
        return this;
    }

    public NpcBuilder setDieMessage(String dieMessage) {
        this.dieMessage = dieMessage;
        return this;
    }

    public NpcBuilder setRoamAreas(Set<Area> roamAreas) {
        this.roamAreas = roamAreas;
        return this;
    }

    public NpcBuilder setValidTriggers(Set<String> validTriggers) {
        this.validTriggers = validTriggers;
        return this;
    }

    public NpcBuilder setLoot(Loot loot) {
        this.loot = loot;
        return this;
    }

    public NpcBuilder setSpawnRules(Set<SpawnRule> spawnRules) {
        this.spawnRules = spawnRules;
        return this;
    }

    public Npc createNpc() {
        return new Npc(gameManager, name, level, colorName, lastPhraseTimestamp, stats, dieMessage, roamAreas, validTriggers, loot, spawnRules);
    }
}