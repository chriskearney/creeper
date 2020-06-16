package com.comandante.creeper.player;

import com.comandante.creeper.world.model.Room;

import java.util.Optional;

public class PlayerMovement {

    private final Player player;
    private final Integer sourceRoomId;
    private final Integer destinationRoomId;
    private final String roomExitMessage;
    private final Optional<Room.Direction> direction;

    private PlayerMovement(Player player,
                          Integer sourceRoomId,
                          Integer destinationRoomId,
                          String roomExitMessage,
                          Room.Direction direction) {
        this.player = player;
        this.sourceRoomId = sourceRoomId;
        this.destinationRoomId = destinationRoomId;
        this.roomExitMessage = roomExitMessage;
        this.direction = Optional.ofNullable(direction);
    }

    public PlayerMovement(Player player,
                          Integer sourceRoomId,
                          Integer destinationRoomId,
                          String roomExitMessage) {
        this(player, sourceRoomId, destinationRoomId, roomExitMessage, null);
    }

    public PlayerMovement(Player player, Integer sourceRoomId, Integer destinationRoomId, Room.Direction direction) {
        this(player, sourceRoomId, destinationRoomId, direction.getExitMessage(), direction);
    }

    public String getRoomExitMessage() {
        return roomExitMessage;
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

    public Optional<Room.Direction> getDirection() {
        return direction;
    }
}
