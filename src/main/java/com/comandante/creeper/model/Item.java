package com.comandante.creeper.model;


import java.io.Serializable;

public class Item implements Serializable {

    private String itemName;
    private String itemDescription;
    private String shortName;
    private String itemId;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    protected Item(String itemName, String itemDescription, String shortName, String itemId) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.shortName = shortName;
        this.itemId = itemId;

    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getShortName() {
        return shortName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}
