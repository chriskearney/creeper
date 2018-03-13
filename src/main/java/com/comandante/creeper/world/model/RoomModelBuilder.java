package com.comandante.creeper.world.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class RoomModelBuilder {
    private int roomId;
    private int floorId;
    private String roomDescription;
    private String roomTitle;
    private Set<String> roomTags = Sets.newHashSet();
    private Set<String> areaNames = Sets.newHashSet();
    private Map<String, String> enterExitNames = Maps.newHashMap();
    private Map<String, String> notables = Maps.newHashMap();
    private Set<String> requiredInternalItemNames = Sets.newHashSet();
    private Integer minimumLevel;

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

    public RoomModelBuilder addEnterExitName(Integer roomId, String enterExitName) {
        enterExitNames.put(String.valueOf(roomId), enterExitName);
        return this;
    }

    public RoomModelBuilder addNotable(String notableName, String description) {
        notables.put(notableName, description);
        return this;
    }

    public RoomModelBuilder setMinimumLevel(Integer level) {
        this.minimumLevel = level;
        return this;
    }

    public RoomModelBuilder addInternalItemName(String internalItemName) {
        this.requiredInternalItemNames.add(internalItemName);
        return this;
    }

    public RoomModel build() {
        return new RoomModel(roomId, floorId, roomDescription, roomTitle, notables, roomTags, areaNames, enterExitNames, requiredInternalItemNames, minimumLevel);
    }
}