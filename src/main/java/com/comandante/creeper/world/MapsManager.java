package com.comandante.creeper.world;

import com.comandante.creeper.server.Color;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.Map;

public class MapsManager {

    private final RoomManager roomManager;
    private final Map<Integer, MapMatrix> floorMatrixMaps;

    public MapsManager(RoomManager roomManager) {
        this.roomManager = roomManager;
        this.floorMatrixMaps = Maps.newHashMap();
    }

    public void generateAllMaps(int maxRows, int maxColumns) {
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Integer roomId = next.getValue().getRoomId();
            String s = drawMap(roomId, new Coords(maxRows, maxColumns));
            next.getValue().setMapData(Optional.of(s));
        }
    }

    public String drawMap(Integer roomId, Coords max) {
        MapMatrix floorMatrix = floorMatrixMaps.get(roomManager.getRoom(roomId).getFloorId());
        MapMatrix mapMatrix = floorMatrix.extractMatrix(roomId, max);
        return mapMatrix.renderMap(roomId, roomManager);
    }

    public static Function<Integer, String> render(final Integer currentroomId, final RoomManager roomManager) {
        return new Function<Integer, String>() {
            @Override
            public String apply(Integer roomId) {
                if (roomId > 0) {
                    if (roomId.equals(currentroomId)) {
                        return "[" + Color.BOLD_ON + Color.RED + "*" + Color.RESET + "]";
                    } else if (roomId.equals(1)) {
                        return "[" + Color.BOLD_ON + Color.BLUE + "L" + Color.RESET + "]";
                    } else if (roomManager.getRoom(roomId).getUpId().isPresent()) {
                        return "[" + Color.GREEN + "^" + Color.RESET + "]";
                    } else if (roomManager.getRoom(roomId).getDownId().isPresent()) {
                        return "[" + Color.GREEN + "v" + Color.RESET + "]";
                    }
                    else {
                        return "[ ]";
                    }
                } else {
                    return " - ";
                }
            }
        };
    }

    public void addFloorMatrix(Integer id, MapMatrix floorMatrix) {
        floorMatrixMaps.put(id, floorMatrix);
    }

    public Map<Integer, MapMatrix> getFloorMatrixMaps() {
        return floorMatrixMaps;
    }
}