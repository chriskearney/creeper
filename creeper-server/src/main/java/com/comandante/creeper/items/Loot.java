package com.comandante.creeper.items;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.Set;

public class Loot implements Serializable {

    private Set<String> internalItemNames;
    private long lootGoldMax;
    private long lootGoldMin;
    private String chestInternalItemName;

    @JsonCreator
    public Loot(@JsonProperty("lootGoldMin") long lootGoldMin,
                @JsonProperty("lootGoldMax") long lootGoldMax,
                @JsonProperty("items") Set<String> internalItemNames,
                @JsonProperty("chestInternalItemNames") String chestInternalItemName) {
        this.internalItemNames = internalItemNames;
        this.lootGoldMax = lootGoldMax;
        this.lootGoldMin = lootGoldMin;
        this.chestInternalItemName = chestInternalItemName;
    }

    @JsonCreator
    public Loot(@JsonProperty("lootGoldMin") long lootGoldMin,
                @JsonProperty("lootGoldMax") long lootGoldMax,
                @JsonProperty("items") Set<String> internalItemNames) {
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
