package com.comandante.creeper.storage;

import com.comandante.creeper.common.CreeperMessage;
import com.comandante.creeper.items.Loot;
import com.comandante.creeper.npc.Temperament;
import com.comandante.creeper.spawner.SpawnRule;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.world.model.Area;

import java.util.Set;

public class NpcMetadata {

    private String name;
    private String colorName;
    private Stats stats;
    private String dieMessage;
    private Temperament temperament;
    private Set<Area> roamAreas;
    private Set<String> validTriggers;
    private Set<SpawnRule> spawnRules;
    private Loot loot;
    // The messages used when dealing damage
    private Set<CreeperMessage> attackMessages;
    // The messages used when landing critical attacks
    private Set<CreeperMessage> criticalAttackMessages;
    // Things the NPC randomly says during battle
    private Set<CreeperMessage> battleMessages;
    // Things that npcs say randomly when idle
    private Set<CreeperMessage> idleMessages;

    public NpcMetadata() {
    }

    public Set<CreeperMessage> getCriticalAttackMessages() {
        return criticalAttackMessages;
    }

    public void setCriticalAttackMessages(Set<CreeperMessage> criticalAttackMessages) {
        this.criticalAttackMessages = criticalAttackMessages;
    }

    public Set<CreeperMessage> getBattleMessages() {
        return battleMessages;
    }

    public void setBattleMessages(Set<CreeperMessage> battleMessages) {
        this.battleMessages = battleMessages;
    }

    public Set<CreeperMessage> getIdleMessages() {
        return idleMessages;
    }

    public void setIdleMessages(Set<CreeperMessage> idleMessages) {
        this.idleMessages = idleMessages;
    }

    public Set<CreeperMessage> getAttackMessages() {
        return attackMessages;
    }

    public void setAttackMessages(Set<CreeperMessage> attackMessages) {
        this.attackMessages = attackMessages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public String getDieMessage() {
        return dieMessage;
    }

    public void setDieMessage(String dieMessage) {
        this.dieMessage = dieMessage;
    }

    public Temperament getTemperament() {
        return temperament;
    }

    public void setTemperament(Temperament temperament) {
        this.temperament = temperament;
    }

    public Set<Area> getRoamAreas() {
        return roamAreas;
    }

    public void setRoamAreas(Set<Area> roamAreas) {
        this.roamAreas = roamAreas;
    }

    public Set<String> getValidTriggers() {
        return validTriggers;
    }

    public void setValidTriggers(Set<String> validTriggers) {
        this.validTriggers = validTriggers;
    }

    public Set<SpawnRule> getSpawnRules() {
        return spawnRules;
    }

    public void setSpawnRules(Set<SpawnRule> spawnRules) {
        this.spawnRules = spawnRules;
    }

    public Loot getLoot() {
        return loot;
    }

    public void setLoot(Loot loot) {
        this.loot = loot;
    }
}


