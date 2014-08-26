package com.comandante.creeper.Items;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.CreeperEntity;

import java.util.Random;
import java.util.Set;

public class ItemSpawner extends CreeperEntity {

    private final ItemType spawnItemType;
    private final ItemSpawnRule itemSpawnRule;
    private final GameManager gameManager;
    private Integer roomId;
    private int noTicks = 0;
    private final Random random = new Random();

    public ItemSpawner(ItemType spawnItemType, ItemSpawnRule itemSpawnRule, GameManager gameManager) {
        this.spawnItemType = spawnItemType;
        this.itemSpawnRule = itemSpawnRule;
        this.gameManager = gameManager;
    }

    public void incTicks() {
        noTicks++;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    @Override
    public void run() {
        incTicks();
        int numberCurrentlyInRoom = 0;
        if (noTicks >= itemSpawnRule.getSpawnIntervalTicks()) {
            Set<String> itemIds = gameManager.getRoomManager().getRoom(roomId).getItemIds();
            for (String i : itemIds) {
                Item currentItem = gameManager.getEntityManager().getItemEntity(i);
                ItemType currentItemType = ItemType.itemTypeFromCode(currentItem.getItemTypeId());
                if (currentItemType.equals(spawnItemType)) {
                    numberCurrentlyInRoom++;
                }
            }
            while (numberCurrentlyInRoom < itemSpawnRule.getMaxPerRoom()) {
                if (itemSpawnRule.getRandomChance().isPresent()) {
                    if (random.nextInt(100) < itemSpawnRule.getRandomChance().get()) {
                        Item item = spawnItemType.create();
                        gameManager.getEntityManager().addItem(item);
                        gameManager.placeItemInRoom(roomId, item.getItemId());
                        numberCurrentlyInRoom++;
                    }
                    continue;
                }
                Item item = spawnItemType.create();
                gameManager.getEntityManager().addItem(item);
                gameManager.placeItemInRoom(roomId, item.getItemId());
                numberCurrentlyInRoom++;
            }
        }
    }
}


}
