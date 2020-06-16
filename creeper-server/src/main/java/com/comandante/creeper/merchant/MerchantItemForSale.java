package com.comandante.creeper.merchant;

public class MerchantItemForSale {
    private final String internalItemName;
    private final int cost;

    public MerchantItemForSale(String internalItemName, int cost) {
        this.internalItemName = internalItemName;
        this.cost = cost;
    }

    public String getInternalItemName() {
        return internalItemName;
    }

    public int getCost() {
        return cost;
    }
}
