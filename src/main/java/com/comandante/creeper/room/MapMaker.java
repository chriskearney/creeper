package com.comandante.creeper.room;

import com.comandante.creeper.server.Color;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapMaker {

    private final RoomManager roomManager;
    private List<List<Optional<Room>>> fullMatrix;
    private final List<List<Integer>> rawCsvMatrix;
    private final static int MAX_ROWS = 9;
    private final static int MAX_COLUMNS = 9;

    public MapMaker(RoomManager roomManager, List<List<Integer>> rawCsvMatrix) {
        this.roomManager = roomManager;
        this.rawCsvMatrix = rawCsvMatrix;
    }

    public void generateAllMaps() {
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Integer roomId = next.getValue().getRoomId();
            String s = drawMap(roomId);
            next.getValue().setMapData(Optional.of(s));
        }
    }

    public String drawMap(Integer roomId) {
        fullMatrix = getBlankMatrix();
        String coords = RoomLayoutCsvPrototype.getCoords(roomId, rawCsvMatrix);
        String[] split = coords.split("\\|");
        int row = Integer.parseInt(split[0]);
        int column = Integer.parseInt(split[1]);
        int destRow = MAX_ROWS / 2;
        int destColumn = MAX_COLUMNS / 2;
        int rowDifference = destRow - row;
        int columnDifference = destColumn - column;
        for (List<Integer> row1 : rawCsvMatrix) {
            for (Integer id : row1) {
                String coords1 = RoomLayoutCsvPrototype.getCoords(id, rawCsvMatrix);
                String[] split1 = coords1.split("\\|");
                row = Integer.parseInt(split1[0]);
                column = Integer.parseInt(split1[1]);
                int targetRow = row + rowDifference;
                int targetColumn = column + columnDifference;
                setCoordinateRoom(targetRow + "|" + targetColumn, roomManager.getRoom(id));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (List<Optional<Room>> next : fullMatrix) {
            Iterator<String> transform = Iterators.transform(next.iterator(), getRendering(roomId));
            while (transform.hasNext()) {
                String s = transform.next();
                sb.append(s);
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public Function<Optional<Room>, String> getRendering(final Integer currentroomId) {
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
                    return " - ";
                }
            }
        };
    }

    public void setCoordinateRoom(String coordinate, Room room) {
        String[] split = coordinate.split("\\|");
        int row = Integer.parseInt(split[0]);
        int column = Integer.parseInt(split[1]);
        if (row < 0 || column < 0 || row > MAX_ROWS || column > MAX_COLUMNS) {
            return;
        }
        fullMatrix.get(row).set(column, Optional.of(room));
    }

    public static List<List<Optional<Room>>> getBlankMatrix() {
        List<List<Optional<Room>>> lists = Lists.newArrayList();
        for (int i = 0; i <= MAX_ROWS; i++) {
            lists.add(Lists.<Optional<Room>>newArrayList());
        }
        for (List<Optional<Room>> roomOpts : lists) {
            for (int i = 0; i <= MAX_COLUMNS; i++) {
                roomOpts.add(Optional.<Room>absent());
            }
        }
        return lists;
    }
}