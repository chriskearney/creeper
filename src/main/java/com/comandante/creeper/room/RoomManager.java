package com.comandante.creeper.room;

import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    private ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();

    public void addRoom(Room room) {
        rooms.put(room.getRoomId(), room);
    }

    public Room getRoom(Integer roomId) {
        return rooms.get(roomId);
    }

    public Iterator<java.util.Map.Entry<Integer, Room>> getRooms() {
        return rooms.entrySet().iterator();
    }

    public Optional<Room> getPlayerCurrentRoom(Player player) {
        Iterator<Map.Entry<Integer, Room>> rooms = getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Room room = next.getValue();
            for (String searchPlayerId : room.getPresentPlayerIds()) {
                if (searchPlayerId.equals(player.getPlayerId())) {
                    return Optional.of(room);
                }
            }
        }
        return Optional.absent();
    }

    public Optional<Room> getNpcCurrentRoom(Npc npc) {
        Iterator<Map.Entry<Integer, Room>> rooms1 = getRooms();
        while (rooms1.hasNext()) {
            Map.Entry<Integer, Room> next = rooms1.next();
            Room room = next.getValue();
            for (String npcId: room.getNpcIds()) {
                if (npcId.equals(npc.getEntityId())) {
                    return Optional.of(next.getValue());
                }
            }
        }
        return Optional.absent();
    }


    public Set<Room> getRoomsByArea(Area area) {
        Set<Room> rooms = Sets.newHashSet();
        Iterator<Map.Entry<Integer, Room>> rooms1 = getRooms();
        while (rooms1.hasNext()) {
            Map.Entry<Integer, Room> next = rooms1.next();
            if (next.getValue().getAreas().contains(area)) {
                rooms.add(next.getValue());
            }
        }
        return rooms;
    }

}
