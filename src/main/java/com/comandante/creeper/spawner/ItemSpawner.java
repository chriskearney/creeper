package com.comandante.creeper.spawner;

import com.codahale.metrics.MetricRegistry;
import com.comandante.creeper.Creeper;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemBuilder;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.world.model.Area;
import com.comandante.creeper.world.model.Room;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class ItemSpawner extends CreeperEntity {

    private final ItemMetadata itemMetadata;
    private final SpawnRule spawnRule;
    private final GameManager gameManager;
    private Integer roomId;
    private int noTicks = 0;
    private final Random random = new Random();
    private final Area spawnArea;


    public ItemSpawner(ItemMetadata itemMetadata, SpawnRule spawnRule, GameManager gameManager) {
        this.itemMetadata = itemMetadata;
        this.spawnRule = spawnRule;
        this.gameManager = gameManager;
        this.noTicks = spawnRule.getSpawnIntervalTicks();
        this.spawnArea = spawnRule.getArea();
    }

    public void incTicks() {
        noTicks++;
    }

    @Override
    public void run() {
        incTicks();
        if (noTicks >= spawnRule.getSpawnIntervalTicks()) {
            int randomPercentage = spawnRule.getRandomChance();
            int numberOfAttempts = spawnRule.getMaxInstances() - counterNumberInArea();
            for (int i = 0; i < numberOfAttempts; i++) {
                if (random.nextInt(100) < randomPercentage || randomPercentage == 100) {
                    if (itemMetadata.getValidTimeOfDays() != null && itemMetadata.getValidTimeOfDays().size() > 0) {
                        if (itemMetadata.getValidTimeOfDays().contains(gameManager.getTimeTracker().getTimeOfDay())) {
                            createAndAddItem();
                        }
                    } else {
                        createAndAddItem();
                    }
                }
                noTicks = 0;
            }
        }
    }

    private void createAndAddItem() {
        ArrayList<Room> rooms = Lists.newArrayList(Iterators.filter(gameManager.getRoomManager().getRoomsByArea(spawnArea).iterator(), getRoomsWithRoom()));
        Room room = rooms.get(random.nextInt(rooms.size()));
        Item item = new ItemBuilder().from(itemMetadata).create();
        gameManager.getEntityManager().saveItem(item);
        gameManager.placeItemInRoom(room.getRoomId(), item.getItemId());
        Creeper.metrics.counter(MetricRegistry.name(ItemSpawner.class, item.getItemName() + "-spawn")).inc();
    }

    private int counterNumberInArea() {
        int numberCurrentlyInArea = 0;
        Set<Room> roomsByArea = gameManager.getRoomManager().getRoomsByArea(spawnArea);
        for (Room room : roomsByArea) {
            if (room.getAreas().contains(spawnArea)) {
                for (String i : room.getItemIds()) {
                    Optional<Item> currentItemOptional = gameManager.getEntityManager().getItemEntity(i);
                    if (!currentItemOptional.isPresent()) {
                        continue;
                    }
                    Item currentItem = currentItemOptional.get();
                    if (currentItem.getInternalItemName().equals(itemMetadata.getInternalItemName())) {
                        numberCurrentlyInArea++;
                    }
                }
            }
        }
        return numberCurrentlyInArea;
    }

    private Predicate<Room> getRoomsWithRoom() {
        return new Predicate<Room>() {
            @Override
            public boolean apply(Room room) {
                int count = 0;
                Set<String> itemIds = room.getItemIds();
                for (String itemId : itemIds) {
                    Optional<Item> itemOptional = gameManager.getEntityManager().getItemEntity(itemId);
                    if (!itemOptional.isPresent()) {
                        continue;
                    }
                    Item item = itemOptional.get();
                    if (item.getInternalItemName().equals(itemMetadata.getInternalItemName())) {
                        count++;
                    }
                }
                if (count < spawnRule.getMaxPerRoom()) {
                    return true;
                }
                return false;
            }
        };
    }
}
