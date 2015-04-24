package com.comandante.creeper.world;

public class RemoteExit {

    public enum Direction {
       UP, DOWN, ENTER
    }

    private final Direction direction;
    private final Integer roomId;
    private final String exitDetail;

    public RemoteExit(Direction direction, Integer roomId, String exitDetail) {
        this.direction = direction;
        this.roomId = roomId;
        this.exitDetail = exitDetail;
    }

    public Direction getDirection() {
        return direction;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public String getExitDetail() {
        return exitDetail;
    }
}
