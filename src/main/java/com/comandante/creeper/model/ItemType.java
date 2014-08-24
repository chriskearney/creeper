package com.comandante.creeper.model;

public enum ItemType {
    KEY(KeyItem.class);
    private final Class itemClass;
    ItemType(Class itemClass) {
        this.itemClass = itemClass;
    }

    public Class getItemClass() {
        return itemClass;
    }
}
