package com.comandante;

public class Movement {

    private final Player player;
    private final Integer sourceRoomId;
    private final Integer destinationRoomId;

    public Movement(Player player, Integer sourceRoomId, Integer destinationRoomId) {
        this.player = player;
        this.sourceRoomId = sourceRoomId;
        this.destinationRoomId = destinationRoomId;
    }

    public Integer getSourceRoomId() {
        return sourceRoomId;
    }

    public Integer getDestinationRoomId() {
        return destinationRoomId;
    }

    public Player getPlayer() {
        return player;
    }
}
