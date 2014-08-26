package com.comandante.creeper.Items;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.CreeperEntity;

import java.util.Set;

public class ItemSpawner extends CreeperEntity {

    private final ItemType spawnItemType;
    private final ItemSpawnRule itemSpawnRule;
    private final GameManager gameManager;
    private final Integer roomId;
    private int noTicks = 0;

    public ItemSpawner(ItemType spawnItemType, ItemSpawnRule itemSpawnRule, GameManager gameManager, Integer roomId) {
        this.spawnItemType = spawnItemType;
        this.itemSpawnRule = itemSpawnRule;
        this.gameManager = gameManager;
        this.roomId = roomId;
    }

    public void incTicks(){
        noTicks++;
    }

    @Override
    public void run() {
        incTicks();
        int numberCurrentlyInRoom = 0;
        if (noTicks >= itemSpawnRule.getSpawnIntervalTicks()) {
            Set<String> itemIds = gameManager.getRoomManager().getRoom(roomId).getItemIds();
            for (String i: itemIds) {
                Item currentItem = gameManager.getEntityManager().getItemEntity(i);
                ItemType currentItemType = ItemType.itemTypeFromCode(currentItem.getItemTypeId());
                if (currentItemType.equals(spawnItemType)) {
                    numberCurrentlyInRoom++;
                }
            }
            if (numberCurrentlyInRoom < itemSpawnRule.getMaxPerRoom()) {
                Item item = spawnItemType.create();
                gameManager.getEntityManager().addItem(item);
                gameManager.placeItemInRoom(roomId, item.getItemId());
            }
        }
    }

}
