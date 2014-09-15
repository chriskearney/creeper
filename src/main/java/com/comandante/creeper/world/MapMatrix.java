package com.comandante.creeper.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MapMatrix {

    private final List<List<Integer>> matrix;
    private Coords max;

    public MapMatrix(List<List<Integer>> matrix) {
        this.matrix = matrix;
        this.max = new Coords(matrix.size(), getMaxColumn());
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
                }
                sb.append(",");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static MapMatrix createMatrixFromCsv(String mapCSV) {
        List<String> rows = Arrays.asList(mapCSV.split("\\r?\\n"));
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
        return new MapMatrix(rowsList);
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
        return new MapMatrix(lists);
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
