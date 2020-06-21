package com.comandante.creeper.items;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Set;

public class Loot implements Serializable {

    private Set<String> internalItemNames;
    private long lootGoldMax;
    private long lootGoldMin;
    private String chestInternalItemName;

    public Loot(long lootGoldMin,
                long lootGoldMax,
                Set<String> internalItemNames,
                String chestInternalItemName) {
        this.internalItemNames = internalItemNames;
        this.lootGoldMax = lootGoldMax;
        this.lootGoldMin = lootGoldMin;
        this.chestInternalItemName = chestInternalItemName;
    }

    public Loot(long lootGoldMin,
                long lootGoldMax,
                Set<String> internalItemNames) {
        this.internalItemNames = internalItemNames;
        this.lootGoldMax = lootGoldMax;
        this.lootGoldMin = lootGoldMin;
    }

    public Loot() {
    }

    public Set<String> getInternalItemNames() {
        if (internalItemNames == null) {
            internalItemNames = Sets.newHashSet();
        }
        return internalItemNames;
    }

    public long getLootGoldMax() {
        return lootGoldMax;
    }

    public long getLootGoldMin() {
        return lootGoldMin;
    }

    public void setItems(Set<String> items) {
        this.internalItemNames = items;
    }

    public void setLootGoldMax(long lootGoldMax) {
        this.lootGoldMax = lootGoldMax;
    }

    public void setLootGoldMin(long lootGoldMin) {
        this.lootGoldMin = lootGoldMin;
    }

    public void setInternalItemNames(Set<String> internalItemNames) {
        this.internalItemNames = internalItemNames;
    }

    public String getChestInternalItemName() {
        return chestInternalItemName;
    }
}
