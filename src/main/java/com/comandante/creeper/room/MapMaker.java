package com.comandante.creeper.room;


import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
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
    private List<List<Optional<Room>>> fullMatrix;

    public MapMaker(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    public String drawMap(Integer roomId) {
        MetricRegistry metricRegistry = new MetricRegistry();
        Timer timer = metricRegistry.timer("draw-map");
        final Timer.Context context = timer.time();
        fullMatrix = getBlankMatrix();
        Room E4 = getRoom(roomId);
        Iterator<Map.Entry<String, Integer>> iterator = getRoomIds(E4.getRoomId(), "4|4").entrySet().iterator();
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
                                        processMapCoordinates(next7);
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
        context.stop();
        //System.out.println("avg map generation time: " + Math.round(timer.getMeanRate()) + "ns");
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
                if (columnNumber < 0 || columnNumber > 7 || row < 0 || row > 7) {
                    return null;
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
                if (stringIntegerMap != null) {
                    return true;
                }
                return false;
            }
        };
    }

    public Map<String, Integer> getRoomIds(Integer roomId, String identifier) {
        Room room = getRoom(roomId);
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
        getRow(row).set(column, Optional.of(room));
    }

    public List<Optional<Room>> getRow(int row) {
        return fullMatrix.get(row);
    }

    private Room getRoom(Integer roomId) {
        return roomManager.getRoom(roomId);
    }

    public static List<List<Optional<Room>>> getBlankMatrix() {
        ArrayList<List<Optional<Room>>> lists =
                Lists.<List<Optional<Room>>>newArrayList(Lists.<Optional<Room>>newArrayList(),
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