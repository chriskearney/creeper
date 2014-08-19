package com.comandante;

public class Movement {

    private final Player player;
    private final Integer sourceRoomId;
    private final Integer destinationRoomId;
    private final String originalMovementCommand;

    public Movement(Player player, Integer sourceRoomId, Integer destinationRoomId, String originalMovementCommand) {
        this.player = player;
        this.sourceRoomId = sourceRoomId;
        this.destinationRoomId = destinationRoomId;
        this.originalMovementCommand = originalMovementCommand;
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

    public String getOriginalMovementCommand() {
        return originalMovementCommand;
    }
}
