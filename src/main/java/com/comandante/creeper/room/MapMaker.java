package com.comandante.creeper.room;

import com.comandante.creeper.server.Color;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapMaker {

    private final RoomManager roomManager;
    private List<List<Optional<Room>>> fullMatrix;
    private final static int MAX_ROWS = 9;
    private final static int MAX_COLUMNS = 9;

    public MapMaker(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public void generateAllMaps() {
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            next.getValue().setMapData(Optional.of(drawMap(next.getValue().getRoomId())));
            System.out.print(".");
        }
    }

    public String drawMap(Integer roomId) {
        fullMatrix = getBlankMatrix();
        Room startingRoom = roomManager.getRoom(roomId);
        final String startingCoords = MAX_ROWS / 2 + "|" + MAX_COLUMNS / 2;
        Iterator<Map.Entry<String, Integer>> iterator = getExitCoordsForRoom(startingRoom.getRoomId(), startingCoords).entrySet().iterator();
        setCoordinateRoom(startingCoords, startingRoom);
        ImmutableList<Map<String, Integer>> maps = FluentIterable.from(ImmutableList.copyOf(iterator))
                .transform(getRoomProcessorFunction())
                .filter(getNonEmpty())
                .toList();
        // TODO : Make this less embarrassing.
        for (Map<String, Integer> next : maps) {
            for (Map<String, Integer> next1 : processMapCoordinates(next)) {
                for (Map<String, Integer> next2 : processMapCoordinates(next1)) {
                    for (Map<String, Integer> next3 : processMapCoordinates(next2)) {
                        for (Map<String, Integer> next4 : processMapCoordinates(next3)) {
                            for (Map<String, Integer> next5 : processMapCoordinates(next4)) {
                                for (Map<String, Integer> next6 : processMapCoordinates(next5)) {
                                    for (Map<String, Integer> next7 : processMapCoordinates(next6)) {
                                        for (Map<String, Integer> next8 : processMapCoordinates(next7)) {
                                            for (Map<String, Integer> next9 : processMapCoordinates(next8)) {
                                                for (Map<String, Integer> next10 : processMapCoordinates(next9)) {
                                                    for (Map<String, Integer> next11 : processMapCoordinates(next10)) {
                                                        for (Map<String, Integer> next12 : processMapCoordinates(next11)) {
                                                            processMapCoordinates(next12);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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


    public List<Map<String, Integer>> scanAndTraverseMap(int i, List<Map<String, Integer>> map) {
        if (i==1) {
            return null;
        }
        for (Map<String, Integer> m: map) {
            i = i - 1;
            return scanAndTraverseMap(i, processMapCoordinates(m)) ;
        }
        return null;
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

    public List<Map<String, Integer>> processMapCoordinates(Map<String, Integer> map) {
        Iterator<Map.Entry<String, Integer>> mapIterator = map.entrySet().iterator();
        return FluentIterable.
                from(ImmutableList.copyOf(mapIterator)).
                transform(getRoomProcessorFunction()).
                filter(getNonEmpty()).toList();
    }

    public Function<Map.Entry<String, Integer>, Map<String, Integer>> getRoomProcessorFunction() {
        return new Function<Map.Entry<String, Integer>, Map<String, Integer>>() {
            @Override
            public Map<String, Integer> apply(Map.Entry<String, Integer> stringIntegerEntry) {
                Integer roomId = stringIntegerEntry.getValue();
                String coords = stringIntegerEntry.getKey();
                String[] split = coords.split("\\|");
                int row = Integer.parseInt(split[0]);
                int columnNumber = Integer.parseInt(split[1]);
                if (columnNumber < 0 || columnNumber > MAX_COLUMNS || row < 0 || row > MAX_ROWS) {
                    return null;
                } else {
                    setCoordinateRoom(coords, roomManager.getRoom(stringIntegerEntry.getValue()));
                    return getExitCoordsForRoom(roomId, coords);
                }
            }
        };
    }

    public Predicate<Map<String, Integer>> getNonEmpty() {
        return new Predicate<Map<String, Integer>>() {
            @Override
            public boolean apply(Map<String, Integer> stringIntegerMap) {
                return stringIntegerMap != null;
            }
        };
    }

    public Map<String, Integer> getExitCoordsForRoom(Integer roomId, String identifier) {
        Room room = roomManager.getRoom(roomId);
        String[] split = identifier.split("\\|");
        int row = Integer.parseInt(split[0]);
        int columnNumber = Integer.parseInt(split[1]);
        Map<String, Integer> roomIds = Maps.newHashMap();
        if (room.getNorthId().isPresent() && !room.getNorthId().get().equals(roomId)) {
            int b = row - 1;
            roomIds.put(b + "|" + columnNumber, room.getNorthId().get());
        }
        if (room.getSouthId().isPresent() && !room.getSouthId().get().equals(roomId)) {
            int b = row + 1;
            roomIds.put(b + "|" + columnNumber, room.getSouthId().get());
        }
        if (room.getEastId().isPresent() && !room.getEastId().get().equals(roomId)) {
            int b = columnNumber + 1;
            roomIds.put(row + "|" + b, room.getEastId().get());
        }
        if (room.getWestId().isPresent() && !room.getWestId().get().equals(roomId)) {
            int b = columnNumber - 1;
            roomIds.put(row + "|" + b, room.getWestId().get());
        }
        return roomIds;
    }

    public void setCoordinateRoom(String coordinate, Room room) {
        String[] split = coordinate.split("\\|");
        int row = Integer.parseInt(split[0]);
        int column = Integer.parseInt(split[1]);
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