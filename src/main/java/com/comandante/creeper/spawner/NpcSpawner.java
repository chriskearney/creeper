package com.comandante.creeper.spawner;


import com.codahale.metrics.MetricRegistry;
import com.comandante.creeper.Creeper;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcBuilder;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.model.Area;
import com.comandante.creeper.world.model.Room;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class NpcSpawner extends CreeperEntity {

    private final Npc npc;
    private final GameManager gameManager;
    private final SpawnRule spawnRule;
    private int noTicks;
    private final Random random = new Random();


    public NpcSpawner(Npc npc, GameManager gameManager, SpawnRule spawnRule) {
        this.npc = npc;
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
            int randomPercentage = spawnRule.getRandomChance();
            int numberOfAttempts = spawnRule.getMaxInstances() - counterNumberInArea(spawnRule.getArea());
            for (int i = 0; i < numberOfAttempts; i++) {
                if (random.nextInt(100) < randomPercentage || randomPercentage == 100) {
                    createAndAddItem(spawnRule.getArea());
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
        List<Room> rooms = gameManager.getRoomManager().getRoomsByArea(spawnArea).stream()
                .filter(findRoomsWithOccupancy(npc))
                .collect(Collectors.toList());
        Room room = rooms.get(random.nextInt(rooms.size()));
        NpcBuilder npcBuilder = new NpcBuilder(npc);
        Npc newNpc = npcBuilder.createNpc();
        newNpc.setCurrentRoom(room);
        gameManager.getEntityManager().addEntity(newNpc);
        room.addPresentNpc(newNpc.getEntityId());
        gameManager.writeToRoom(room.getRoomId(), newNpc.getColorName() + " appears." + "\r\n");
        room.getPresentPlayers().forEach(Player::processNpcAggro);
        Creeper.metrics.counter(MetricRegistry.name(NpcSpawner.class, npc.getName() + "-spawn")).inc();
    }

    private Predicate<Room> findRoomsWithOccupancy(Npc npc) {
        return room -> {
            long count = room.getNpcIds().stream()
                    .map(npcId -> gameManager.getEntityManager().getNpcEntity(npcId))
                    .filter(n -> n.getName().equals(npc.getName()))
                    .count();

            return count < spawnRule.getMaxPerRoom();
        };
    }
}