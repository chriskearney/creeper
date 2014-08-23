package com.comandante.creeper.managers;

import com.comandante.creeper.model.Npc;
import com.comandante.creeper.model.NpcType;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
import com.google.common.base.Optional;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NPCManager {

    private final ConcurrentHashMap<String, Npc> npcs = new ConcurrentHashMap<>();
    private final RoomManager roomManager;
    private final PlayerManager playerManager;
    private final ExecutorService phraserService;

    public NPCManager(RoomManager roomManager, PlayerManager playerManager) {
        this.roomManager = roomManager;
        this.playerManager = playerManager;
        this.phraserService = Executors.newSingleThreadExecutor();
        phraserService.submit(new Phraser());
    }

    public void saveNpc(Npc npc) {
        npcs.put(npc.getNpcId(), npc);
    }

    public Npc getNpc(String npcId) {
        return npcs.get(npcId);
    }

    public Optional<Room> getNpcCurrentRoom(String npcId) {
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Room room = next.getValue();
            if (room.getNpcIds().contains(npcId)) {
                return Optional.of(room);
            }
        }
        return Optional.absent();
    }

    class Phraser implements Runnable {
        private Random randomGenerator = new Random();
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(5000);
                    for (Map.Entry<String, Npc> next : npcs.entrySet()) {
                        Npc npc = next.getValue();
                        NpcType npcType = npc.getNpcType();
                        long phraseTimestamp = npc.getPhraseTimestamp();
                        long phraseInterval = npcType.getPhrasesIntervalMs();
                        long now = System.currentTimeMillis();
                        if (now - phraseTimestamp > phraseInterval) {
                            String phrase = npcType.getPhrases().get(randomGenerator.nextInt(npcType.getPhrases().size()));
                            Optional<Room> roomOpt = getNpcCurrentRoom(npc.getNpcId());
                            if (roomOpt.isPresent()) {
                                Set<Player> presentPlayers = playerManager.getPresentPlayers(roomOpt.get());
                                for (Player player : presentPlayers) {
                                    player.getChannel().write(npcType.getNpcName() + ": " + phrase + "\r\n");
                                    npc.setPhraseTimestamp(System.currentTimeMillis());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}
