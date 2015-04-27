package com.comandante.creeper.Items;

import java.io.Serializable;
import java.util.Set;

public class Loot implements Serializable {

    private final Set<Item> items;
    private final int lootGoldMax;
    private final int lootGoldMin;

    public Loot(int lootGoldMin, int lootGoldMax, Set<Item> items) {
        this.items = items;
        this.lootGoldMax = lootGoldMax;
        this.lootGoldMin = lootGoldMin;
    }

    public Set<Item> getItems() {
        return items;
    }

    public int getLootGoldMax() {
        return lootGoldMax;
    }

    public int getLootGoldMin() {
        return lootGoldMin;
    }
}
