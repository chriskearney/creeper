package com.comandante.creeper.items;


import com.comandante.creeper.player.PlayerClass;
import com.comandante.creeper.stats.Stats;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Sets;

import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Equipment {

    private EquipmentSlotType equipmentSlotType;
    private Stats statsIncreaseWhileEquipped;
    private Set<PlayerClass> allowedPlayerClass;

    public Equipment(EquipmentSlotType equipmentSlotType, Stats statsIncreaseWhileEquipped, Set<PlayerClass> allowedPlayerClass) {
        this.equipmentSlotType = equipmentSlotType;
        this.statsIncreaseWhileEquipped = statsIncreaseWhileEquipped;
        this.allowedPlayerClass = allowedPlayerClass;
    }

    public Equipment() {
    }

    // Future self: I am doing these stupid annoying hacks to preserve existing player's inventories and equipment
    public Equipment(Equipment equipment) {
        this.equipmentSlotType = equipment.equipmentSlotType;
        this.statsIncreaseWhileEquipped = equipment.statsIncreaseWhileEquipped;
        if (equipment.allowedPlayerClass != null) {
            this.allowedPlayerClass = Sets.newHashSet(equipment.allowedPlayerClass);
        } else {
            this.allowedPlayerClass = Sets.newHashSet();
        }

    }

    public EquipmentSlotType getEquipmentSlotType() {
        return equipmentSlotType;
    }

    public Stats getStats() {
        return statsIncreaseWhileEquipped;
    }

    public Stats getStatsIncreaseWhileEquipped() {
        return statsIncreaseWhileEquipped;
    }

    public Set<PlayerClass> getAllowedPlayerClass() {
        if (allowedPlayerClass == null) {
            return Sets.newHashSet();
        }
        return allowedPlayerClass;
    }

    public void setEquipmentSlotType(EquipmentSlotType equipmentSlotType) {
        this.equipmentSlotType = equipmentSlotType;
    }

    public void setStatsIncreaseWhileEquipped(Stats statsIncreaseWhileEquipped) {
        this.statsIncreaseWhileEquipped = statsIncreaseWhileEquipped;
    }

    public void setAllowedPlayerClass(Set<PlayerClass> allowedPlayerClass) {
        this.allowedPlayerClass = allowedPlayerClass;
    }
}
