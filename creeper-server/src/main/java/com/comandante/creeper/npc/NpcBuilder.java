package com.comandante.creeper.npc;

import com.comandante.creeper.common.CreeperMessage;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Loot;
import com.comandante.creeper.spawner.SpawnRule;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.storage.NpcMetadata;
import com.comandante.creeper.world.model.Area;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class NpcBuilder {
    private GameManager gameManager;
    private String name;
    private String colorName;
    private long lastPhraseTimestamp;
    private Stats stats;
    private String dieMessage;
    private Set<Area> roamAreas;
    private Set<String> validTriggers;
    private Loot loot;
    private Set<SpawnRule> spawnRules;
    private Temperament temperament;
    // The messages used when dealing damage
    private Set<CreeperMessage> attackMessages;
    // The messages used when landing critical attacks
    private Set<CreeperMessage> criticalAttackMessages;
    // Things the NPC randomly says during battle
    private Set<CreeperMessage> battleMessages;
    // Things that npcs say randomly when idle
    private Set<CreeperMessage> idleMessages;
    private String originalJsonFilename;

    public NpcBuilder() {
    }

    public NpcBuilder(Npc npc) {
        this.name = npc.getName();
        this.colorName = npc.getColorName();
        this.lastPhraseTimestamp = npc.getLastPhraseTimestamp();
        this.stats = new Stats(npc.getStats());
        this.dieMessage = npc.getDieMessage();
        this.roamAreas = npc.getRoamAreas();
        this.validTriggers = npc.getValidTriggers();
        this.loot = npc.getLoot();
        this.spawnRules = npc.getSpawnRules();
        this.gameManager = npc.getGameManager();
        this.temperament = npc.getTemperament();
        this.attackMessages = npc.getAttackMessages();
        this.criticalAttackMessages = npc.getCriticalAttackMessages();
        this.battleMessages = npc.getBattleMessages();
        this.idleMessages = npc.getIdleMessages();
    }

    public NpcBuilder(NpcMetadata npcMetadata) {
        this.name = npcMetadata.getName();
        this.colorName = npcMetadata.getColorName();
        this.stats = new Stats(npcMetadata.getStats());
        this.dieMessage = npcMetadata.getDieMessage();
        this.roamAreas = npcMetadata.getRoamAreas();
        this.validTriggers = npcMetadata.getValidTriggers();
        this.loot = npcMetadata.getLoot();
        this.spawnRules = npcMetadata.getSpawnRules();
        this.temperament = npcMetadata.getTemperament();
        this.attackMessages = npcMetadata.getAttackMessages();
        this.criticalAttackMessages = npcMetadata.getCriticalAttackMessages();
        this.battleMessages = npcMetadata.getBattleMessages();
        this.idleMessages = npcMetadata.getIdleMessages();
        this.originalJsonFilename = npcMetadata.getOriginalJsonFilename();
    }

    public NpcBuilder setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
        return this;
    }

    public NpcBuilder setName(String name) {
        this.name = name;
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

    public NpcBuilder setTemperament(Temperament temperament) {
        this.temperament = temperament;
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

    public NpcBuilder setAttackMessages(Set<CreeperMessage> attackMessages) {
        this.attackMessages = attackMessages;
        return this;
    }

    public NpcBuilder setCriticalAttackMessages(Set<CreeperMessage> criticalAttackMessages) {
        this.criticalAttackMessages = criticalAttackMessages;
        return this;
    }

    public NpcBuilder setBattleMessages(Set<CreeperMessage> battleMessages) {
        this.battleMessages = battleMessages;
        return this;
    }

    public NpcBuilder setIdleMessages(Set<CreeperMessage> idleMessages) {
        this.idleMessages = idleMessages;
        return this;
    }

    public NpcBuilder setOriginalJsonFilename(String originalJsonFilename) {
        this.originalJsonFilename = originalJsonFilename;
        return this;
    }

    public Npc createNpc() {
        checkNotNull(gameManager);
        if (loot != null ) {
            if (loot.getLootGoldMin() > loot.getLootGoldMax()) {
                throw new RuntimeException("Invalid loot configuration.");
            }
        }
        Optional<BufferedImage> npcArt = gameManager.getNpcStorage().getNpcArt(originalJsonFilename);
        if (npcArt.isPresent()) {
            System.out.println("hi");
        }
        return new Npc(gameManager, name, colorName, lastPhraseTimestamp, stats, dieMessage, temperament, roamAreas, validTriggers, loot, spawnRules, attackMessages, criticalAttackMessages, battleMessages, idleMessages, npcArt);
    }
}