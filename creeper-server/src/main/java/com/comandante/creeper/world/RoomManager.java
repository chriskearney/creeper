package com.comandante.creeper.world;

import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.world.model.Area;
import com.comandante.creeper.world.model.Room;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    private final PlayerManager playerManager;
    private ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<>();
    public RoomManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public void addRoom(Room room) {
        rooms.put(room.getRoomId(), room);
    }

    public Optional<List<Integer>> getRoomsForTag(String tag) {
        List<Integer> matchedRooms = Lists.newArrayList();
        Iterator<Map.Entry<Integer, Room>> rooms = getRoomsIterator();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            if (next.getValue().getRoomTags().contains(tag)) {
                matchedRooms.add(next.getValue().getRoomId());
            }
        }

        if (matchedRooms.size() > 0) {
            return Optional.of(matchedRooms);
        } else return Optional.empty();
    }

    public Iterator<Map.Entry<Integer, Room>> getRoomsIterator() {
        return rooms.entrySet().iterator();
    }

    public void addMerchant(Merchant merchant) {
        for (Integer roomId: merchant.getRoomIds()) {
            getRoom(roomId).addMerchant(merchant);
        };
    }

    public Room getRoom(Integer roomId) {
        return rooms.get(roomId);
    }

    public void tagRoom(Integer roomId, String tag) {
        getRoom(roomId).addTag(tag);
    }

    public Set<String> getTagsForRoom(Integer roomId) {
        return getRoom(roomId).getRoomTags();
    }

    public Map<Integer, Room> getrooms() {
        return rooms;
    }

    public Set<Room> getRoomsByFloorId(Integer floorId) {
        Set<Room> rooms = Sets.newHashSet();
        Iterator<Map.Entry<Integer, Room>> rooms1 = getRoomsIterator();
        while (rooms1.hasNext()) {
            Map.Entry<Integer, Room> next = rooms1.next();
            if (next.getValue().getFloorId().equals(floorId)) {
                rooms.add(next.getValue());
            }
        }
        return rooms;
    }

    public Optional<Room> getPlayerCurrentRoom(Player player) {
        if (player.getCurrentRoom() != null) {
            return Optional.of(player.getCurrentRoom());
        }
        return getPlayerCurrentRoom(player.getPlayerId());
    }

    public Optional<Room> getPlayerCurrentRoom(String playerId) {
        Iterator<Map.Entry<Integer, Room>> rooms = getRoomsIterator();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Room room = next.getValue();
            for (String searchPlayerId : room.getPresentPlayerIds()) {
                if (searchPlayerId.equals(playerId)) {
                    return Optional.of(room);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Room> getNpcCurrentRoom(Npc npc) {
        return Optional.of(npc.getCurrentRoom());
    }


    public Set<Room> getRoomsByArea(Area area) {
        Set<Room> rooms = Sets.newHashSet();
        Iterator<Map.Entry<Integer, Room>> rooms1 = getRoomsIterator();
        while (rooms1.hasNext()) {
            Map.Entry<Integer, Room> next = rooms1.next();
            if (next.getValue().getAreas().contains(area)) {
                rooms.add(next.getValue());
            }
        }
        return rooms;
    }

    public boolean doesRoomIdExist(Integer roomId) {
        Iterator<Map.Entry<Integer, Room>> rooms1 = getRoomsIterator();
        Set<Integer> roomIds = Sets.newHashSet();
        while (rooms1.hasNext()) {
            Map.Entry<Integer, Room> next = rooms1.next();
            if (next.getKey().equals(roomId)) {
                return true;
            }
        }
        return false;
    }

    public Room getRoomByItemId(String itemId) {
        Iterator<Map.Entry<Integer, Room>> rooms = getRoomsIterator();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            if (next.getValue().getItemIds().contains(itemId)) {
                return next.getValue();
            }
        }
        return null;
    }
}

