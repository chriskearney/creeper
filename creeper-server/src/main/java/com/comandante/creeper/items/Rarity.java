package com.comandante.creeper.items;


public enum Rarity {

    OFTEN("often", 40),
    BASIC("basic", 15.0),
    UNCOMMON("uncommon", 7),
    RARE("rare", 3),
    LEGENDARY("legendary", 1),
    EXOTIC("exotic", .5);

    private final String rarityTypeName;
    private final double percentToLoot;

    Rarity(String rarityTypeName, double percentToLoot) {
        this.rarityTypeName = rarityTypeName;
        this.percentToLoot = percentToLoot;
    }

    public String getRarityTypeName() {
        return rarityTypeName;
    }

    public double getPercentToLoot() {
        return percentToLoot;
    }
}
