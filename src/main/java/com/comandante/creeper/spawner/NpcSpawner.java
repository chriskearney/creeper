package com.comandante.creeper.spawner;


import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.world.Area;
import com.comandante.creeper.world.Room;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class NpcSpawner extends CreeperEntity {

    private final Npc npc;
    private final GameManager gameManager;
    private final SpawnRule spawnRule;
    private int noTicks;
    private final Random random = new Random();
    private final Set<Area> spawnAreas;


    public NpcSpawner(Npc npc, Set<Area> area, GameManager gameManager, SpawnRule spawnRule) {
        this.npc = npc;
        this.spawnAreas = area;
        this.gameManager = gameManager;
        this.spawnRule = spawnRule;
        this.noTicks = spawnRule.getSpawnIntervalTicks();
    }

    public void incTicks() {
        noTicks++;
    }

    @Override
    public void run() {
        incTicks();
        if (noTicks >= spawnRule.getSpawnIntervalTicks()) {
            for (Area spawnArea : spawnAreas) {
                int randomPercentage = spawnRule.getRandomChance();
                int numberOfAttempts = spawnRule.getMaxInstances() - counterNumberInArea(spawnArea);
                for (int i = 0; i < numberOfAttempts; i++) {
                    if (random.nextInt(100) < randomPercentage || randomPercentage == 100) {
                        createAndAddItem(spawnArea);
                    }
                }
            }
            noTicks = 0;
        }
    }

    private int counterNumberInArea(Area spawnArea) {
        int numberCurrentlyInArea = 0;
        Set<Room> roomsByArea = gameManager.getRoomManager().getRoomsByArea(spawnArea);
        for (Room room : roomsByArea) {
            if (room.getAreas().contains(spawnArea)) {
                for (String i : room.getNpcIds()) {
                    Npc currentNpc = gameManager.getEntityManager().getNpcEntity(i);
                    if (currentNpc.getName().equals(npc.getName())) {
                        numberCurrentlyInArea++;
                    }
                }
            }
        }
        return numberCurrentlyInArea;
    }

    private void createAndAddItem(Area spawnArea) {
        ArrayList<Room> rooms = Lists.newArrayList(Iterators.filter(gameManager.getRoomManager().getRoomsByArea(spawnArea).iterator(), getRoomsWithRoom()));
        Room room = rooms.get(random.nextInt(rooms.size()));
        Npc newNpc = npc.create(gameManager, npc.getLoot());
        gameManager.getEntityManager().addEntity(newNpc);
        room.addPresentNpc(newNpc.getEntityId());
    }

    private Predicate<Room> getRoomsWithRoom() {
        return new Predicate<Room>() {
            @Override
            public boolean apply(Room room) {
                int count = 0;
                Set<String> npcIds = room.getNpcIds();
                for (String npcId : npcIds) {
                    Npc npcEntity = gameManager.getEntityManager().getNpcEntity(npcId);
                    if (npcEntity.getName().equals(npc.getName())) {
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