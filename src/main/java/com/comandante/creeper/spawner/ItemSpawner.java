package com.comandante.creeper.spawner;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.entity.CreeperEntity;

import java.util.Random;
import java.util.Set;

public class ItemSpawner extends CreeperEntity {

    private final ItemType spawnItemType;
    private final SpawnRule spawnRule;
    private final GameManager gameManager;
    private Integer roomId;
    private int noTicks = 0;
    private final Random random = new Random();

    public ItemSpawner(ItemType spawnItemType, SpawnRule spawnRule, GameManager gameManager) {
        this.spawnItemType = spawnItemType;
        this.spawnRule = spawnRule;
        this.gameManager = gameManager;
        this.noTicks = spawnRule.getSpawnIntervalTicks();
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
        if (noTicks >= spawnRule.getSpawnIntervalTicks()) {
            processRandom();
            noTicks = 0;
        }
    }

    private void processRandom() {
        int randomPercentage = spawnRule.getRandomChance();
        int numberOfAttempts = spawnRule.getMaxInstances() - countNumberInRoom();
        for (int i = 0; i < numberOfAttempts; i++) {
            if (random.nextInt(100) < randomPercentage || randomPercentage == 100) {
                createAndAddItem();
            }
        }
    }

    private void processNormal() {
        int numberToCreate = spawnRule.getMaxInstances() - countNumberInRoom();
        for (int i = 0; i < numberToCreate; i++) {
            createAndAddItem();
        }
    }

    private int countNumberInRoom() {
        int numberCurrentlyInRoom = 0;
        Set<String> itemIds = gameManager.getRoomManager().getRoom(roomId).getItemIds();
        for (String i : itemIds) {
            Item currentItem = gameManager.getEntityManager().getItemEntity(i);
            ItemType currentItemType = ItemType.itemTypeFromCode(currentItem.getItemTypeId());
            if (currentItemType.equals(spawnItemType)) {
                numberCurrentlyInRoom++;
            }
        }
        return numberCurrentlyInRoom;
    }

    private void createAndAddItem() {
        Item item = spawnItemType.create();
        gameManager.getEntityManager().addItem(item);
        gameManager.placeItemInRoom(roomId, item.getItemId());
    }

}
