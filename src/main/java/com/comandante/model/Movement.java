package com.comandante.model;

import com.comandante.command.CommandType;

public class Movement {

    private final Player player;
    private final Integer sourceRoomId;
    private final Integer destinationRoomId;
    private final CommandType originalMovementCommand;

    public Movement(Player player, Integer sourceRoomId, Integer destinationRoomId, CommandType originalMovementCommand) {
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

    public CommandType getOriginalMovementCommand() {
        return originalMovementCommand;
    }
}
