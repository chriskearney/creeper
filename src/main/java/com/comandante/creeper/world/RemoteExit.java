package com.comandante.creeper.world;

public class RemoteExit {

    public enum Direction {
       UP, DOWN
    }

    private final Direction direction;
    private final Integer roomId;

    public RemoteExit(Direction direction, Integer roomId) {
        this.direction = direction;
        this.roomId = roomId;
    }

    public Direction getDirection() {
        return direction;
    }

    public Integer getRoomId() {
        return roomId;
    }
}
