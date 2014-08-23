package com.comandante.creeper.model;

import com.comandante.creeper.command.commands.MovementCommand;

public class Movement {

    private final Player player;
    private final Integer sourceRoomId;
    private final Integer destinationRoomId;
    private final MovementCommand command;
    private final String roomExitMessage;

    public Movement(Player player,
                    Integer sourceRoomId,
                    Integer destinationRoomId,
                    MovementCommand command,
                    String roomExitMessage) {
        this.player = player;
        this.sourceRoomId = sourceRoomId;
        this.destinationRoomId = destinationRoomId;
        this.command = command;
        this.roomExitMessage = roomExitMessage;
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

    public String getRoomExitMessage() {
        return roomExitMessage;
    }

    public MovementCommand getCommand() {
        return command;
    }
}
