package com.comandante.creeper.api;

public class CreeperClientData {

    private final String map;
    private final String prompt;
    private final String lookSelf;
    private final String inventory;
    private final String gossip;

    public CreeperClientData(String map, String prompt, String lookSelf, String inventory, String gossip) {
        this.map = map;
        this.prompt = prompt;
        this.lookSelf = lookSelf;
        this.inventory = inventory;
        this.gossip = gossip;
    }

    public String getMap() {
        return map;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getLookSelf() {
        return lookSelf;
    }

    public String getInventory() {
        return inventory;
    }

    public String getGossip() {
        return gossip;
    }
}
