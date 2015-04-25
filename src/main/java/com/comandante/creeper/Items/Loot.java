package com.comandante.creeper.Items;

import java.io.Serializable;
import java.util.Set;

public class Loot implements Serializable {

    private final Set<Item> items;
    private final int lootGold;

    public Loot(int lootGold, Set<Item> items) {
        this.lootGold = lootGold;
        this.items = items;
    }

    public Set<Item> getItems() {
        return items;
    }

    public int getLootGold() {
        return lootGold;
    }
}
