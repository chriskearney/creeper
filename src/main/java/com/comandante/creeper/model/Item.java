package com.comandante.creeper.model;


public abstract class Item extends CreeperEntity {

    private final String itemName;
    private final String itemDescription;
    private final ItemType itemType;
    private final String shortName;

    protected Item(String itemName, String itemDescription, ItemType itemType, String shortName) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemType = itemType;
        this.shortName = shortName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getShortName() {
        return shortName;
    }
}
