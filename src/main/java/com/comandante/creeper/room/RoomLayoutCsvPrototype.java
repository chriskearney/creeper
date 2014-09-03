package com.comandante.creeper.room;


import com.comandante.creeper.entity.EntityManager;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomLayoutCsvPrototype {
    public static String mapCSV = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,8517,8518,8519,8520,8521\n" +
            ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,9017,9018,9019,9020,9021\n" +
            ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,9517,9518,9519,9520,9521\n" +
            ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,10017,10018,10019,10020,10021\n" +
            ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,10517,10518,10519,10520,10521\n" +
            "2005,2006,2007,2008,2009,2010,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,11017,11018,11019,11020,11021\n" +
            "2505,2506,2507,2508,2509,2510,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,10006,10007,10008,10009,10010,9511,11514,11515,11516,11517,11518,11519,11520,11521\n" +
            "3005,3006,3007,3008,3009,3010,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,10505,,,,,,,,,,,,,\n" +
            "3505,3506,3507,3508,3509,3510,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,11005,,,,,,,,,,,,,\n" +
            "4005,4006,4007,4008,4009,4010,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,11505,,,,,,,,,,,,,\n" +
            "4505,4506,4507,4508,4509,4510,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,12005,,,,,,,,,,,,,\n" +
            "5005,5006,5007,5008,5009,5010,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,12505,,,,,,,,,,,,,\n" +
            "5505,5506,5507,5508,5509,5510,,,,,,,,,25506,,,,25007,,,,,,,,,,,,20510,,,,,,,13005,,,,,,,,,,,,,\n" +
            "6005,6006,6007,6008,6009,6010,509,510,511,512,513,514,515,516,517,518,519,520,521,3,4,26509,6,1,8,9,10,11,12,13,21010,15,16,17,18,19,20,21,,,,,,,,,,,,,\n" +
            "6505,6506,6507,6508,6509,6510,,,,,,,,,26006,,,,,,,,,,,,,,,,21510,,,,,,,,,,,,,,,,,,,,\n" +
            "7005,7006,7007,7008,7009,7010,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
            "7505,7506,7507,7508,7509,7510,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
            "8005,8006,8007,8008,8009,8010,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
            "8505,8506,8507,8508,8509,8510,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
            "9005,9006,9007,9008,9009,9010,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n" +
            "9505,9506,9507,9508,9509,9510,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,\n";

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

    public static void main(String[] args) throws IOException {
        int totalRows = 500;
        int toalColums = 500;

        List<List<Integer>> rows = Lists.newArrayList();

        for (int i = 0; i < totalRows; i++) {
            rows.add(Lists.<Integer>newArrayList());
        }
        int id = 1;
        FileWriter fileWriter = new FileWriter(new File("/tmp/bigmap.csv"));
        for (List<Integer> row : rows) {
            for (int i = 0; i < toalColums; i++) {
                id++;
                fileWriter.write(id + ",");
            }
            fileWriter.write("\n");
        }

        fileWriter.close();
    }

}
