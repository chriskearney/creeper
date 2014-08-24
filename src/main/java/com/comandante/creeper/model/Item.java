package com.comandante.creeper.model;


import com.comandante.creeper.managers.GameManager;

public abstract class Item extends CreeperEntity {

    private final GameManager gameManager;
    private final String itemName;
    private final String itemDescription;

    protected Item(GameManager gameManager, String itemName, String itemDescription) {
        this.gameManager = gameManager;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }
}
