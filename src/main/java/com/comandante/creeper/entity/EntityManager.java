package com.comandante.creeper.entity;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemSerializer;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.room.Room;
import com.comandante.creeper.room.RoomManager;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntityManager {

    private final ConcurrentHashMap<String, Npc> npcs = new ConcurrentHashMap<>();
    private final HTreeMap<String, Item> items;
    private final ConcurrentHashMap<String, CreeperEntity> entities = new ConcurrentHashMap<>();
    private final ExecutorService tickService = Executors.newFixedThreadPool(1);
    private final ExecutorService ticketRunnerService = Executors.newFixedThreadPool(10);
    private final RoomManager roomManager;
    private final PlayerManager playerManager;
    private final DB db;

    public EntityManager(RoomManager roomManager, PlayerManager playerManager, DB db) {
        this.roomManager = roomManager;
        if (db.exists("itemMap")) {
            this.items = db.get("itemMap");
        } else {
            this.items = db.createHashMap("itemMap").valueSerializer(new ItemSerializer()).make();
        }
        this.playerManager = playerManager;
        this.db = db;
        tickService.submit(new Ticker());
    }

    public void addEntity(CreeperEntity creeperEntity) {
        if (creeperEntity instanceof Npc) {
            Npc npc = (Npc) creeperEntity;
            npcs.put(creeperEntity.getEntityId(), npc);
        } else if (creeperEntity instanceof Room) {
            roomManager.addRoom((Room) creeperEntity);
        } else {
            entities.put(creeperEntity.getEntityId(), creeperEntity);
        }
    }

    public void addItem(Item item) {
        items.put(item.getItemId(), item);
        db.commit();
    }

    public void removeItem(Item item) {
        items.remove(item.getItemId());
        db.commit();
    }

    public void deleteNpcEntity(String npcId) {
        roomManager.getNpcCurrentRoom(getNpcEntity(npcId)).get().removePresentNpc(npcId);
        npcs.remove(npcId);
    }

    public Npc getNpcEntity(String npcId) {
        return npcs.get(npcId);
    }

    public void updateNpcHealth(String npcId, int amt) {
        synchronized (npcId){
            Npc npcEntity = getNpcEntity(npcId);
            npcEntity.getStats().setCurrentHealth(npcEntity.getStats().getCurrentHealth() + amt);
        }
    }

    public Item getItemEntity(String itemId) {
        return items.get(itemId);
    }

    class Ticker implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
                    while (rooms.hasNext()) {
                        Map.Entry<Integer, Room> next = rooms.next();
                        ticketRunnerService.submit(next.getValue());
                    }
                    Iterator<Map.Entry<String, Player>> players = playerManager.getPlayers();
                    while (players.hasNext()) {
                        Map.Entry<String, Player> next = players.next();
                        ticketRunnerService.submit(next.getValue());
                    }
                    for (Map.Entry<String, Npc> next : npcs.entrySet()) {
                        ticketRunnerService.submit(next.getValue());
                    }
                    Iterator<Map.Entry<String, CreeperEntity>> iterator = entities.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, CreeperEntity> next = iterator.next();
                        ticketRunnerService.submit(next.getValue());
                    }
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    throw new RuntimeException("Problem with ticker.");
                }
            }
        }
    }

}
