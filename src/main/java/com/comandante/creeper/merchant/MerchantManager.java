package com.comandante.creeper.merchant;


import com.comandante.creeper.Items.Item;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class MerchantManager {

    private final GameManager gameManager;

    public MerchantManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void purchaseItem(Merchant merchant, int itemNo, Player player) {
        Iterator<Map.Entry<Integer, MerchantItemForSale>> merchantItemForSales = merchant.getMerchantItemForSales().entrySet().iterator();
        while (merchantItemForSales.hasNext()) {
            Map.Entry<Integer, MerchantItemForSale> next = merchantItemForSales.next();
            if (next.getKey().equals(itemNo)) {
                int price = next.getValue().getCost();
                int availableGold = gameManager.getPlayerManager().getPlayerMetadata(player.getPlayerId()).getGold();
                if (availableGold >= price) {
                    Item item = next.getValue().getItem().create();
                    gameManager.getEntityManager().addItem(item);
                    gameManager.acquireItem(player, item.getItemId());
                    gameManager.getPlayerManager().incrementGold(player.getPlayerId(), -price);
                    gameManager.getChannelUtils().write(player.getPlayerId(), "You have purchased: " + item.getItemName() + "\r\n");
                } else {
                    gameManager.getChannelUtils().write(player.getPlayerId(), "You can't afford: " + next.getValue().getItem().getItemName() + "\r\n");
                }
            }
        }
    }
}
