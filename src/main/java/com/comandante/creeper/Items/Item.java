package com.comandante.creeper.Items;


import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {

    private String itemName;
    private String itemDescription;
    private List<String> itemTriggers;
    private String restingName;
    private String itemId;
    private Integer itemTypeId;
    private int numberOfUses;
    private boolean isWithPlayer;

    protected Item(String itemName, String itemDescription, List<String> itemTriggers, String restingName, String itemId, Integer itemTypeId, int numberOfUses, boolean isWithPlayer) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemTriggers = itemTriggers;
        this.restingName = restingName;
        this.itemId = itemId;
        this.itemTypeId = itemTypeId;
        this.numberOfUses = numberOfUses;
    }

    public boolean isWithPlayer() {
        return isWithPlayer;
    }

    public void setWithPlayer(boolean isWithPlayer) {
        this.isWithPlayer = isWithPlayer;
    }

    public int getNumberOfUses() {
        return numberOfUses;
    }

    public void setNumberOfUses(int numberOfUses) {
        this.numberOfUses = numberOfUses;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Integer getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(Integer itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public List<String> getItemTriggers() {
        return itemTriggers;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemTriggers(List<String> itemTriggers) {
        this.itemTriggers = itemTriggers;
    }

    public String getRestingName() {
        return restingName;
    }

    public void setRestingName(String restingName) {
        this.restingName = restingName;
    }
}
