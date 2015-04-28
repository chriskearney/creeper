package com.comandante.creeper.merchant;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.managers.GameManager;

import java.util.Map;
import java.util.Set;


public class MerchantItemForSale {
    private final ItemType itemType;
    private final int cost;

    public MerchantItemForSale(ItemType itemType, int cost) {
        this.itemType = itemType;
        this.cost = cost;
    }

    public ItemType getItem() {
        return itemType;
    }

    public int getCost() {
        return cost;
    }
}
