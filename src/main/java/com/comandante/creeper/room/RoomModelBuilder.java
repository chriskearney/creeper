package com.comandante.creeper.room;

import java.util.Set;

public class RoomModelBuilder {
    private int roomId;
    private int floorId;
    private String roomDescription;
    private String roomTitle;
    private Set<String> roomTags;

    public RoomModelBuilder setRoomId(int roomId) {
        this.roomId = roomId;
        return this;
    }

    public RoomModelBuilder setFloorId(int floorId) {
        this.floorId = floorId;
        return this;
    }

    public RoomModelBuilder setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
        return this;
    }

    public RoomModelBuilder setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
        return this;
    }

    public RoomModelBuilder setRoomTags(Set<String> roomTags) {
        this.roomTags = roomTags;
        return this;
    }

    public RoomModel build() {
        return new RoomModel(roomId, floorId, roomDescription, roomTitle, roomTags);
    }
}