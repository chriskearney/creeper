package com.comandante.creeper.model;


import com.comandante.creeper.Items.SpawnRule;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.npc.Npc;

import java.util.Random;
import java.util.Set;

public class NpcSpawner extends CreeperEntity {

    private final Npc npc;
    private final GameManager gameManager;
    private final SpawnRule spawnRule;
    private int noTicks = 0;
    private final Random random = new Random();
    private Integer roomId;

    public NpcSpawner(Npc npc, GameManager gameManager, SpawnRule spawnRule) {
        this.npc = npc;
        this.gameManager = gameManager;
        this.spawnRule = spawnRule;
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
            if (spawnRule.getRandomChance().isPresent()) {
                processRandom();
            } else {
                processNormal();
            }
            noTicks = 0;
        }
    }

    private void processRandom() {
        int randomPercentage = spawnRule.getRandomChance().get();
        int numberOfAttempts = spawnRule.getMaxPerRoom() - countNumberInRoom();
        for (int i = 0; i < numberOfAttempts; i++) {
            if (random.nextInt(100) < randomPercentage) {
                createAndAddItem();
            }
        }
    }

    private void processNormal() {
        int numberToCreate = spawnRule.getMaxPerRoom() - countNumberInRoom();
        for (int i = 0; i < numberToCreate; i++) {
            createAndAddItem();
        }
    }

    private int countNumberInRoom() {
        int numberCurrentlyInRoom = 0;
        Set<String> npcIds = gameManager.getRoomManager().getRoom(roomId).getNpcIds();
        for (String i : npcIds) {
            Npc currentNpc = gameManager.getEntityManager().getNpcEntity(i);
            if (currentNpc.getName().equals(npc.getName())) {
                numberCurrentlyInRoom++;
            }
        }
        return numberCurrentlyInRoom;
    }

    private void createAndAddItem() {
        Npc newNpc = npc.create(gameManager, roomId);
        gameManager.getEntityManager().addEntity(newNpc);
    }
}