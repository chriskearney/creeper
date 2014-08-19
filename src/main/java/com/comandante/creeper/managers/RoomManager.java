package com.comandante.creeper.managers;

import com.comandante.creeper.model.Room;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {

    private ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();

    public void addRoom(Room room) {
        rooms.put(room.roomId, room);
    }

    public Room getRoom(Integer roomId) {
        return rooms.get(roomId);
    }

    public Iterator<java.util.Map.Entry<Integer, Room>> getRooms() {
        return rooms.entrySet().iterator();
    }



}
