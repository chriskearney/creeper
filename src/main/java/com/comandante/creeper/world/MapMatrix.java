package com.comandante.creeper.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapMatrix {

    private final List<List<Integer>> matrix;
    private final Map<Integer, Set<RemoteExit>> remotes;
    private Coords max;

    public MapMatrix(List<List<Integer>> matrix, Map<Integer, Set<RemoteExit>> remotes) {
        this.matrix = matrix;
        this.remotes = remotes;
        this.max = new Coords(matrix.size(), getMaxColumn());
    }

    public Map<Integer, Set<RemoteExit>> getRemotes() {
        return remotes;
    }

    public void addRemote(Integer roomId, RemoteExit exit) {
        if (remotes.get(roomId) == null) {
            remotes.put(roomId, Sets.newHashSet(exit));
        } else {
            remotes.get(roomId).add(exit);
        }
    }

    private static void addRemote(Integer roomId, RemoteExit exit, Map<Integer, Set<RemoteExit>> remotes) {
        if (remotes.get(roomId) == null) {
            remotes.put(roomId, Sets.newHashSet(exit));
        } else {
            remotes.get(roomId).add(exit);
        }
    }

    public int getMaxRow() {
        return max.row;
    }

    public int getMaxCol() {
        return max.column;
    }

    public void setMax(Coords max) {
        this.max = max;
    }

    public java.util.Iterator<List<Integer>> getRows() {
        return matrix.iterator();
    }

    public Coords getCoords(Integer roomId) {
        int row = 0;
        int column = 0;
        for (List<Integer> r : matrix) {
            for (Integer id : r) {
                if (id.equals(roomId)) {
                    return new Coords(row, column);
                } else {
                    column++;
                }
            }
            row++;
            column = 0;
        }
        return null;
    }

    public void setCoordsValue(Coords coords, Integer roomId) {
        if (coords.row < 0 || coords.column < 0 || coords.row >= max.row || coords.column >= max.column) {
            return;
        }
        matrix.get(coords.row).set(coords.column, roomId);
    }


    public Integer getNorth(Integer sourceId) {
        Coords coords = getCoords(sourceId);
        int rowNorth = coords.row - 1;
        int columnNorth = coords.column;
        return getId(rowNorth, columnNorth);
    }

    public Integer getSouth(Integer sourceId) {
        Coords coords = getCoords(sourceId);
        int rowSouth = coords.row + 1;
        int columnSouth = coords.column;
        return getId(rowSouth, columnSouth);
    }

    public Integer getEast(Integer sourceId) {
        Coords coords = getCoords(sourceId);
        int rowEast = coords.row;
        int columnEast = coords.column + 1;
        return getId(rowEast, columnEast);
    }

    public Integer getWest(Integer sourceId) {
        Coords coords = getCoords(sourceId);
        int rowWest = coords.row;
        int columnWest = coords.column - 1;
        return getId(rowWest, columnWest);
    }

    private Integer getId(int row, int column) {
        if (row < 0 || column < 0 || row >= max.row || column >= max.column) {
            return 0;
        }
        List<Integer> integers = matrix.get(row);
        return integers.get(column);
    }

    private int getMaxColumn() {
        int max = 0;
        for (List<Integer> list : matrix) {
            if (list.size() > max) {
                max = list.size();
            }
        }
        return max;
    }

    public MapMatrix extractMatrix(Integer roomId, Coords newMax) {
        MapMatrix destinationMatrix = getBlankMatrix(newMax.row, newMax.column);
        Coords coords = getCoords(roomId);
        int rowDifference = destinationMatrix.getMaxRow() / 2 - coords.row;
        int columnDifference = destinationMatrix.getMaxCol() / 2 - coords.column;
        Iterator<List<Integer>> rows = getRows();
        while (rows.hasNext()) {
            UnmodifiableIterator<Integer> ids = Iterators.filter(rows.next().iterator(), removeZeros());
            while (ids.hasNext()) {
                Integer id = ids.next();
                Coords currentMatrixCoords = getCoords(id);
                Coords destinationMatrixCoords = new Coords(currentMatrixCoords.row + rowDifference,
                        currentMatrixCoords.column + columnDifference);
                destinationMatrix.setCoordsValue(destinationMatrixCoords, id);
            }
        }
        return destinationMatrix;
    }

    private Predicate<Integer> removeZeros() {
        return new Predicate<Integer>() {
            @Override
            public boolean apply(Integer integer) {
                if (integer > 0) {
                    return true;
                }
                return false;
            }
        };
    }


    public String getCsv() {
        StringBuilder sb = new StringBuilder();
        for (List<Integer> list : matrix) {
            for (Integer roomId : list) {
                if (!roomId.equals(0)) {
                    sb.append(roomId);
                    if (remotes.containsKey(roomId)) {
                        for (RemoteExit exit : remotes.get(roomId)) {
                            if (exit.getDirection().equals(RemoteExit.Direction.UP)) {
                                sb.append("u").append(exit.getRoomId());
                            } else if (exit.getDirection().equals(RemoteExit.Direction.DOWN)) {
                                sb.append("d").append(exit.getRoomId());
                            }
                        }
                    }
                }
                sb.append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static Integer getUp(String csvInputCell) {
        String[] us = csvInputCell.split("u");
        if (us[1].matches(".*[a-zA-Z]+.*")) {
            return Integer.valueOf(us[1].split("[a-zA-Z]")[0]);
        }
        return Integer.valueOf(us[1]);
    }

    private static Integer getDown(String csvInputCell) {
        String[] us = csvInputCell.split("d");
        if (us[1].matches(".*[a-zA-Z]+.*")) {
            return Integer.valueOf(us[1].split("[a-zA-Z]")[0]);
        }
        return Integer.valueOf(us[1]);
    }

    public static MapMatrix createMatrixFromCsv(String mapCSV) {
        List<String> rows = Arrays.asList(mapCSV.split("\\r?\\n"));
        ArrayList<List<Integer>> rowsList = Lists.newArrayList();
        Map<Integer, Set<RemoteExit>> remotes = Maps.newHashMap();
        for (String row : rows) {
            List<String> strings = Arrays.asList(row.split(",", -1));
            List<Integer> data = Lists.newArrayList();
            for (String string : strings) {
                if (!string.isEmpty()) {
                    Integer roomId = Integer.parseInt(string.split("[a-zA-Z]")[0]);
                    if (string.contains("u")) {
                        Integer up = getUp(string);
                        addRemote(roomId, new RemoteExit(RemoteExit.Direction.UP, up), remotes);
                    }
                    if (string.contains("d")) {
                        Integer down = getDown(string);
                        addRemote(roomId, new RemoteExit(RemoteExit.Direction.DOWN, down), remotes);
                    }
                    data.add(roomId);
                } else {
                    data.add(0);
                }
            }
            rowsList.add(data);
        }
        return new MapMatrix(rowsList, remotes);
    }

    private static MapMatrix getBlankMatrix(int maxRows, int maxColumns) {
        List<List<Integer>> lists = Lists.newArrayList();
        for (int i = 0; i <= maxRows; i++) {
            lists.add(Lists.<Integer>newArrayList());
        }
        for (List<Integer> roomOpts : lists) {
            for (int i = 0; i <= maxColumns; i++) {
                roomOpts.add(0);
            }
        }
        return new MapMatrix(lists, Maps.<Integer, Set<RemoteExit>>newHashMap());
    }

    public String renderMap(Integer roomId, RoomManager roomManager) {
        StringBuilder sb = new StringBuilder();
        Iterator<List<Integer>> rows = getRows();
        while (rows.hasNext()) {
            List<Integer> next = rows.next();
            Iterator<String> transform = Iterators.transform(next.iterator(), MapsManager.render(roomId, roomManager));
            while (transform.hasNext()) {
                String s = transform.next();
                sb.append(s);
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }

    public void addRow(boolean startOfArray) {
        ArrayList<Integer> newRow = Lists.<Integer>newArrayList();
        for (int i = 0; i < matrix.get(0).size(); i++) {
            newRow.add(0);
        }
        if (startOfArray) {
            matrix.add(0, newRow);
        } else {
            matrix.add(newRow);
        }
        setMax(new Coords(matrix.size(), matrix.get(0).size()));
    }

    public void addColumn(boolean startOfArray) {
        Iterator<List<Integer>> rows = getRows();
        while (rows.hasNext()) {
            List<Integer> next = rows.next();
            if (startOfArray) {
                next.add(0, 0);
            } else {
                next.add(0);
            }
        }
        setMax(new Coords(matrix.size(), matrix.get(0).size()));
    }
}
