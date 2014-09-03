package com.comandante.creeper.room;


import com.comandante.creeper.entity.EntityManager;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomLayoutCsvPrototype {
    public static final String mapCSV =
            ",,,,,,,,,,,,,,,,,,,,\n" +
                    ",,,,,,,,,,,,,,,,,,,,\n" +
                    ",,,,,,,,,,,,,,,,,,,,\n" +
                    ",,,,,,,,,,,,,,,,,,,,\n" +
                    ",,,,,,,,,,,,,,,,,,,,\n" +
                    ",,,,,,,,,,,,,,,,,,,,\n" +
                    ",,,,,61,60,59,58,57,53,52,51,50,49,48,47,46,45,44,43\n" +
                    ",,,,,62,,,,,54,,,,,,,,,,42\n" +
                    ",,,,64,63,,,,,55,,,,,,,,,,41\n" +
                    ",,,,65,,,,,,56,,,,,,,,,,40\n" +
                    ",,,,66,,25,24,23,22,21,20,19,18,17,16,,,,,39\n" +
                    ",,,,67,,26,,,,,,,,,15,,,,,38\n" +
                    ",,,,68,,27,,,,,,,,,14,33,34,35,36,37\n" +
                    ",,71,70,69,,28,,,,,,,,,13,,,,,\n" +
                    ",,72,,,,29,30,31,32,7,8,9,10,11,12,,,,,\n" +
                    ",,73,,,,,,,,6,,86,,,,,,,,\n" +
                    ",,74,,,,,,,,5,,85,,,,,,,,\n" +
                    ",,75,76,77,78,79,80,81,82,4,83,84,,,,,,,,\n" +
                    ",,,,,,,,,,3,,,,,,,,,,\n" +
                    ",,,,,,,,,,2,,,,,,,,,,\n" +
                    ",,,,,,,,,,1,,,,,,,,,,\n";

    public static List<List<Integer>> convertMapData(String mapCSV) {
        List<String> rows = Arrays.asList(mapCSV.split("\n"));
        ArrayList<List<Integer>> rowsList = Lists.newArrayList();
        for (String row : rows) {
            List<String> strings = Arrays.asList(row.split(",", -1));
            List<Integer> data = Lists.newArrayList();
            for (String string : strings) {
                if (!string.isEmpty()) {
                    data.add(Integer.parseInt(string));
                } else {
                    data.add(0);
                }
            }
            rowsList.add(data);
        }
        return rowsList;
    }

    public static List<List<Integer>> buildRooms(EntityManager entityManager) {
        List<List<Integer>> lists = convertMapData(mapCSV);
        List<BasicRoom> rooms = Lists.newArrayList();
        for (List<Integer> list : lists) {
            for (Integer newRoomId : list) {
                if (newRoomId == 0) {
                    continue;
                }
                BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder();
                basicRoomBuilder.setRoomId(newRoomId);
                basicRoomBuilder.setRoomDescription("This is a temporary description.");
                basicRoomBuilder.setRoomTitle("The generated room-" + newRoomId);
                if (getNorth(lists, newRoomId) > 0) {
                    basicRoomBuilder.setNorthId(Optional.of(getNorth(lists, newRoomId)));
                }
                if (getEast(lists, newRoomId) > 0) {
                    basicRoomBuilder.setEastId(Optional.of(getEast(lists, newRoomId)));
                }
                if (getSouth(lists, newRoomId) > 0) {
                    basicRoomBuilder.setSouthId(Optional.of(getSouth(lists, newRoomId)));
                }
                if (getWest(lists, newRoomId) > 0) {
                    basicRoomBuilder.setWestId(Optional.of(getWest(lists, newRoomId)));
                }
                rooms.add(basicRoomBuilder.createBasicRoom());
            }
        }
        for (Room room : rooms) {
            entityManager.addEntity(room);
        }
        return lists;
    }

    private static Integer getNorth(List<List<Integer>> matrix, Integer sourceId) {
        String coords = getCoords(sourceId, matrix);
        String[] split = coords.split("\\|");
        int row = Integer.parseInt(split[0]);
        int column = Integer.parseInt(split[1]);
        int rowNorth = row - 1;
        int columnNorth = column;
        return getId(rowNorth, columnNorth, matrix);
    }

    private static Integer getSouth(List<List<Integer>> matrix, Integer sourceId) {
        String coords = getCoords(sourceId, matrix);
        String[] split = coords.split("\\|");
        int row = Integer.parseInt(split[0]);
        int column = Integer.parseInt(split[1]);
        int rowSouth = row + 1;
        int columnSouth = column;
        return getId(rowSouth, columnSouth, matrix);
    }

    private static Integer getEast(List<List<Integer>> matrix, Integer sourceId) {
        String coords = getCoords(sourceId, matrix);
        String[] split = coords.split("\\|");
        int row = Integer.parseInt(split[0]);
        int column = Integer.parseInt(split[1]);
        int rowEast = row;
        int columnEast = column + 1;
        return getId(rowEast, columnEast, matrix);
    }

    private static Integer getWest(List<List<Integer>> matrix, Integer sourceId) {
        String coords = getCoords(sourceId, matrix);
        String[] split = coords.split("\\|");
        int row = Integer.parseInt(split[0]);
        int column = Integer.parseInt(split[1]);
        int rowWest = row;
        int columnWest = column - 1;
        return getId(rowWest, columnWest, matrix);
    }

    private static Integer getId(int row, int column, List<List<Integer>> matrix) {
        if (row < 0 || column < 0 || row >= matrix.size() || column >= getMaxColumn(matrix)) {
            return 0;
        }
        List<Integer> integers = matrix.get(row);
        return integers.get(column);
    }

    private static int getMaxColumn(List<List<Integer>> matrix) {
        int max = 0;
        for (List<Integer> list : matrix) {
            if (list.size() > max) {
                max = list.size();
            }
        }
        return max;
    }


    public static String getCoords(Integer roomId, List<List<Integer>> matrix) {
        int row = 0;
        int column = 0;
        for (List<Integer> r : matrix) {
            for (Integer id : r) {
                if (id.equals(roomId)) {
                    return row + "|" + column;
                } else {
                    column++;
                }
            }
            row++;
            column = 0;
        }
        return null;
    }

}
