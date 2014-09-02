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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapMaker {

    private final RoomManager roomManager;
    List<List<Optional<Room>>> fullMatrix;

    public MapMaker(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public String drawMap(Integer roomId) {
        fullMatrix = getBlankMatrix();
        Room E4 = getRoom(roomId);
        int c = 0;
        Iterator<Map.Entry<String, Integer>> iterator = getRoomIds(E4.getRoomId(), "4|4").entrySet().iterator();
        ImmutableList<Map<String, Integer>> maps = FluentIterable.from(ImmutableList.copyOf(iterator))
                .transform(getRoomProcessorFunction())
                .filter(getNonEmpty())
                .toList();
        for (Map<String, Integer> next : maps) {
            c++;
            Iterator<Map.Entry<String, Integer>> iterator1 = next.entrySet().iterator();
            ImmutableList<Map<String, Integer>> maps1 = FluentIterable.from(ImmutableList.copyOf(iterator1)).transform(getRoomProcessorFunction()).filter(getNonEmpty()).toList();
            for (Map<String, Integer> next1 : maps1) {
                c++;
                Iterator<Map.Entry<String, Integer>> iterator2 = next1.entrySet().iterator();
                ImmutableList<Map<String, Integer>> maps2 = FluentIterable.from(ImmutableList.copyOf(iterator2)).transform(getRoomProcessorFunction()).filter(getNonEmpty()).toList();
                for (Map<String, Integer> next2 : maps2) {
                    c++;
                    Iterator<Map.Entry<String, Integer>> iterator3 = next2.entrySet().iterator();
                    ImmutableList<Map<String, Integer>> maps3 = FluentIterable.from(ImmutableList.copyOf(iterator3)).transform(getRoomProcessorFunction()).filter(getNonEmpty()).toList();
                    for (Map<String, Integer> next3 : maps3) {
                        c++;
                        Iterator<Map.Entry<String, Integer>> iterator4 = next3.entrySet().iterator();
                        ImmutableList<Map<String, Integer>> maps4 = FluentIterable.from(ImmutableList.copyOf(iterator4)).transform(getRoomProcessorFunction()).filter(getNonEmpty()).toList();
                        for (Map<String, Integer> next4 : maps4) {
                            c++;
                            Iterator<Map.Entry<String, Integer>> iterator5 = next4.entrySet().iterator();
                            ImmutableList<Map<String, Integer>> maps5 = FluentIterable.from(ImmutableList.copyOf(iterator5)).transform(getRoomProcessorFunction()).filter(getNonEmpty()).toList();
                            for (Map<String, Integer> next5 : maps5) {
                                c++;
                                Iterator<Map.Entry<String, Integer>> iterator6 = next5.entrySet().iterator();
                                ImmutableList<Map<String, Integer>> maps6 = FluentIterable.from(ImmutableList.copyOf(iterator6)).transform(getRoomProcessorFunction()).filter(getNonEmpty()).toList();
                                for (Map<String, Integer> next6 : maps6) {
                                    c++;
                                    Iterator<Map.Entry<String, Integer>> iterator7 = next6.entrySet().iterator();
                                    ImmutableList<Map<String, Integer>> maps7 = FluentIterable.from(ImmutableList.copyOf(iterator7)).transform(getRoomProcessorFunction()).filter(getNonEmpty()).toList();
                                    for (Map<String, Integer> next7 : maps7) {
                                        c++;
                                        Iterator<Map.Entry<String, Integer>> iterator8 = next7.entrySet().iterator();
                                        ImmutableList<Map<String, Integer>> maps8 = FluentIterable.from(ImmutableList.copyOf(iterator8)).transform(getRoomProcessorFunction()).filter(getNonEmpty()).toList();
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
        System.out.println("Count - " + c);
        return sb.toString();
    }

    public Function<Optional<Room>, String> getRendering(final Integer currentroomId) {
        return new Function<Optional<Room>, String>() {
            @Override
            public String apply(Optional<Room> roomOptional) {
                if (roomOptional.isPresent()) {
                    if (roomOptional.get().getRoomId().equals(currentroomId)) {
                        return "[" + Color.BOLD_ON + Color.RED + "*" + Color.RESET + "]";
                    } else {
                        return "[ ]";
                    }
                } else {
                    return " - ";
                }
            }
        };
    }

    public Function<Map.Entry<String, Integer>, Map<String, Integer>> getRoomProcessorFunction() {
        return new Function<Map.Entry<String, Integer>, Map<String, Integer>>() {
            @Override
            public Map<String, Integer> apply(Map.Entry<String, Integer> stringIntegerEntry) {
                Integer roomId = stringIntegerEntry.getValue();
                String coords = stringIntegerEntry.getKey();
                String[] split = coords.split("\\|");
                int row;
                int columnNumber;
                try {
                    row = Integer.parseInt(split[0]);
                    columnNumber = Integer.parseInt(split[1]);
                } catch (Exception e) {
                    return Maps.newHashMap();
                }
                if (columnNumber < 0 || columnNumber > 7) {
                    return Maps.newHashMap();
                }
                if (row < 0 || row > 7) {
                    return Maps.newHashMap();
                } else {
                    setCoordinateRoom(coords, getRoom(stringIntegerEntry.getValue()));
                    return getRoomIds(roomId, coords);
                }
            }
        };
    }

    public Predicate<Map<String, Integer>> getNonEmpty() {
        return new Predicate<Map<String, Integer>>() {
            @Override
            public boolean apply(Map<String, Integer> stringIntegerMap) {
                if (stringIntegerMap.size() > 0) {
                    return true;
                }
                return false;
            }
        };
    }

    public Map<String, Integer> getRoomIds(Integer roomId, String identifier) {
        Room room = getRoom(roomId);
        String[] split = identifier.split("\\|");
        int row;
        int columnNumber;

        row = Integer.parseInt(split[0]);
        columnNumber = Integer.parseInt(split[1]);

        Map<String, Integer> roomIds = Maps.newHashMap();
        if (room.getNorthId().isPresent()) {
            int b = row - 1;
            roomIds.put(b + "|" + columnNumber, room.getNorthId().get());
        }
        if (room.getSouthId().isPresent()) {
            int b = row + 1;
            roomIds.put(b + "|" + columnNumber, room.getSouthId().get());
        }
        if (room.getEastId().isPresent()) {
            int b = columnNumber + 1;
            roomIds.put(row + "|" + b, room.getEastId().get());
        }
        if (room.getWestId().isPresent()) {
            int b = columnNumber - 1;
            roomIds.put(row + "|" + b, room.getWestId().get());
        }
        return roomIds;
    }

    public void setCoordinateRoom(String coordinate, Room room) {
        String[] split = coordinate.split("\\|");
        int row = Integer.parseInt(split[0]);
        int column = Integer.parseInt(split[1]);
        getRow(row).set(column, Optional.of(room));
    }

    public List<Optional<Room>> getRow(int row) {
        return fullMatrix.get(row);
    }


    public void populateRowsWithEmpty() {
        for (List<Optional<Room>> roomOpts : fullMatrix) {
            for (int i = 0; i <= 7; i++) {
                roomOpts.add(Optional.<Room>absent());
            }
        }
    }

    private Room getRoom(Integer roomId) {
        return roomManager.getRoom(roomId);
    }

    public static List<List<Optional<Room>>> getBlankMatrix() {
        ArrayList<List<Optional<Room>>> lists = Lists.<List<Optional<Room>>>newArrayList(Lists.<Optional<Room>>newArrayList(),
                Lists.<Optional<Room>>newArrayList(), Lists.<Optional<Room>>newArrayList(),
                Lists.<Optional<Room>>newArrayList(), Lists.<Optional<Room>>newArrayList(),
                Lists.<Optional<Room>>newArrayList(), Lists.<Optional<Room>>newArrayList(),
                Lists.<Optional<Room>>newArrayList());

        for (List<Optional<Room>> roomOpts : lists) {
            for (int i = 0; i <= 7; i++) {
                roomOpts.add(Optional.<Room>absent());
            }
        }
        return lists;
    }
}
