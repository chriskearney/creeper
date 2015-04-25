package com.comandante.creeper.entity;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemDecayManager;
import com.comandante.creeper.Items.ItemSerializer;
import com.comandante.creeper.fight.FightResults;
import com.comandante.creeper.fight.FightResultsBuilder;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.world.RoomManager;
import com.google.common.base.Optional;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EntityManager {

    private final ConcurrentHashMap<String, Npc> npcs = new ConcurrentHashMap<>();
    private final HTreeMap<String, Item> items;
    private final ConcurrentHashMap<String, CreeperEntity> entities = new ConcurrentHashMap<>();
    private final ExecutorService tickService = Executors.newFixedThreadPool(1);
    private final ExecutorService ticketRunnerService = Executors.newFixedThreadPool(10);
    private final RoomManager roomManager;
    private final PlayerManager playerManager;
    private final DB db;
    private final ChannelUtils channelUtils;
    private final ItemDecayManager itemDecayManager;

    public EntityManager(RoomManager roomManager, PlayerManager playerManager, DB db, ChannelUtils channelUtils) {
        this.roomManager = roomManager;
        if (db.exists("itemMap")) {
            this.items = db.get("itemMap");
        } else {
            this.items = db.createHashMap("itemMap").valueSerializer(new ItemSerializer()).make();
        }
        this.playerManager = playerManager;
        this.db = db;
        tickService.submit(new Ticker());
        this.channelUtils = channelUtils;
        this.itemDecayManager = new ItemDecayManager(this);
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

    public void updateNpcHealth(String npcId, int amt, String playerId) {
        Player player = playerManager.getPlayer(playerId);
        synchronized (npcId){
            Npc npc = getNpcEntity(npcId);
            if (npc != null) {
                npc.getStats().setCurrentHealth(npc.getStats().getCurrentHealth() + amt);
                if (npc.getStats().getCurrentHealth() <= 0) {
                    playerManager.getSessionManager().getSession(playerId).setActiveFight(Optional.<Future<FightResults>>absent());
                    int experience = playerManager.getPlayerMetadata(playerId).getStats().getExperience();
                    experience += npc.getStats().getExperience();
                    channelUtils.write(playerId, "You killed " + npc.getName() + " (" + npc.getStats().getExperience() + "exp)", true);
                    channelUtils.writeToRoom(playerId, npc.getDieMessage());
                    playerManager.getPlayerMetadata(playerId).getStats().setExperience(experience);
                    Item corpse = new Item(npc.getName() + " corpse", "a bloody corpse.", Arrays.asList("corpse"), "a corpse lies on the ground.", UUID.randomUUID().toString(), Item.CORPSE_ID_RESERVED, 0, false, 120, npc.getLoot());
                    addItem(corpse);
                    roomManager.getRoom(roomManager.getPlayerCurrentRoom(player).get().getRoomId()).addPresentItem(getItemEntity(corpse.getItemId()).getItemId());
                    itemDecayManager.addItem(corpse);
                    deleteNpcEntity(npc.getEntityId());
                }
            }
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
