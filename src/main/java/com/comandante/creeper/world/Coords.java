package com.comandante.creeper.world;

public class Coords {

    public final int row;
    public final int column;

    public Coords(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
