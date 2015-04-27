package com.comandante.creeper.Items;

import java.util.Random;

public class LootManager {

    private final Random  random = new Random();

    private int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public int lootGoldAmountReturn(Loot loot) {
        return randInt(loot.getLootGoldMin(), loot.getLootGoldMax());
    }

}
