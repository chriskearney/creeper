package com.comandante.creeper.room;

import com.comandante.creeper.server.Color;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapMaker {

    private final RoomManager roomManager;
    private final Map<Integer, List<List<Integer>>> floorMatrixMaps;

    public MapMaker(RoomManager roomManager) {
        this.roomManager = roomManager;
        this.floorMatrixMaps = Maps.newHashMap();
    }

    public void generateAllMaps(int maxRows, int maxColumns) {
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Integer roomId = next.getValue().getRoomId();
            String s = drawMap(roomId, maxRows, maxColumns);
            next.getValue().setMapData(Optional.of(s));
        }
    }

    private String drawMap(Integer roomId, int maxRows, int maxColumns) {
        List<List<Optional<Room>>> destinationMatrix = getBlankMatrix(maxRows, maxColumns);
        List<List<Integer>> rawCsvFloorMatrix = floorMatrixMaps.get(roomManager.getRoom(roomId).getFloorId());
        Coords coords = RoomLayoutCsvPrototype.getCoords(roomId, rawCsvFloorMatrix);
        // find the center of the desintation matrix and determine the formula
        int rowDifference = maxRows / 2 - coords.row;
        int columnDifference = maxColumns / 2 - coords.column;
        for (List<Integer> row : rawCsvFloorMatrix) {
            for (Integer id : row) {
                if (id == 0) {
                    //"Blank" Room as far as CSV land goes. ex 0,0,0,54,0,0,3
                    continue;
                }
                Coords currentMatrixCoords = RoomLayoutCsvPrototype.getCoords(id, rawCsvFloorMatrix);
                Coords destinationMatrixCoords = new Coords(currentMatrixCoords.row + rowDifference,
                        currentMatrixCoords.column + columnDifference);
                if (areCoordsWithinMatrixRange(destinationMatrixCoords, maxRows, maxColumns)) {
                    destinationMatrix.get(destinationMatrixCoords.row).set(destinationMatrixCoords.column,
                            Optional.of(roomManager.getRoom(id)));
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (List<Optional<Room>> next : destinationMatrix) {
            Iterator<String> transform = Iterators.transform(next.iterator(), getRendering(roomId));
            while (transform.hasNext()) {
                String s = transform.next();
                sb.append(s);
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    private boolean areCoordsWithinMatrixRange(Coords coords, int maxRow, int maxColumn) {
        if (coords.row < 0 || coords.column < 0 || coords.row > maxRow || coords.column > maxColumn) {
            return false;
        }
        return true;
    }

    private Function<Optional<Room>, String> getRendering(final Integer currentroomId) {
        return new Function<Optional<Room>, String>() {
            @Override
            public String apply(Optional<Room> roomOptional) {
                if (roomOptional.isPresent()) {
                    if (roomOptional.get().getRoomId().equals(currentroomId)) {
                        return "[" + Color.BOLD_ON + Color.RED + "*" + Color.RESET + "]";
                    } else if (roomOptional.get().getRoomId().equals(1)) {
                        return "[" + Color.BOLD_ON + Color.BLUE + "L" + Color.RESET + "]";
                    } else if (roomOptional.get().getUpId().isPresent() && roomOptional.get().getDownId().isPresent()) {
                        return "[" + Color.BOLD_ON + Color.GREEN + ":" + Color.RESET + "]";
                    } else if (roomOptional.get().getUpId().isPresent() || roomOptional.get().getDownId().isPresent()) {
                        return "[" + Color.BOLD_ON + Color.GREEN + "." + Color.RESET + "]";
                    }
                    else {
                        return "[ ]";
                    }
                } else {
                    return "   ";
                }
            }
        };
    }

    private List<List<Optional<Room>>> getBlankMatrix(int maxRows, int maxColumns) {
        List<List<Optional<Room>>> lists = Lists.newArrayList();
        for (int i = 0; i <= maxRows; i++) {
            lists.add(Lists.<Optional<Room>>newArrayList());
        }
        for (List<Optional<Room>> roomOpts : lists) {
            for (int i = 0; i <= maxColumns; i++) {
                roomOpts.add(Optional.<Room>absent());
            }
        }
        return lists;
    }

    public void addFloorMatrix(Integer id, List<List<Integer>> floorMatrix){
        floorMatrixMaps.put(id, floorMatrix);
    }
}