package com.comandante.creeper.world;

import com.google.common.collect.Sets;

import java.util.Set;

public class RoomModelBuilder {
    private int roomId;
    private int floorId;
    private String roomDescription;
    private String roomTitle;
    private Set<String> roomTags = Sets.newHashSet();
    private Set<String> areaNames = Sets.newHashSet();

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

    public void setRoomTags(Set<String> roomTags) {
        this.roomTags = roomTags;
    }

    public RoomModelBuilder addRoomTag(String roomTag) {
        roomTags.add(roomTag);
        return this;
    }

    public RoomModelBuilder addAreaName(String areaName) {
        areaNames.add(areaName);
        return this;
    }

    public RoomModel build() {
        return new RoomModel(roomId, floorId, roomDescription, roomTitle, roomTags, areaNames);
    }
}