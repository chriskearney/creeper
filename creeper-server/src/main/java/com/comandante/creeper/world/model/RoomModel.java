package com.comandante.creeper.world.model;

import com.google.gson.GsonBuilder;

import java.util.Map;
import java.util.Set;

public class RoomModel {

    int roomId;
    int floorId;
    String roomDescription;
    String roomTitle;
    Set<String> roomTags;
    Set<String> areaNames;
    Map<String, String> enterExitNames;
    Map<String, String> notables;
    Set<String> requiredInternalItemNames;
    Integer minimumLevel;


    public RoomModel(int roomId, int floorId, String roomDescription, String roomTitle, Map<String, String> notables, Set<String> roomTags, Set<String> areaNames, Map<String, String> enterExitNames, Set<String> requiredInternalItemNames, Integer minimumLevel) {
        this.roomId = roomId;
        this.floorId = floorId;
        this.roomDescription = roomDescription;
        this.roomTitle = roomTitle;
        this.roomTags = roomTags;
        this.areaNames = areaNames;
        this.enterExitNames = enterExitNames;
        this.notables = notables;
        this.requiredInternalItemNames = requiredInternalItemNames;
        this.minimumLevel = minimumLevel;
    }

    public Set<String> getRequiredInternalItemNames() {
        return requiredInternalItemNames;
    }

    public Integer getMinimumLevel() {
        return minimumLevel;
    }

    public void setNotables(Map<String, String> notables) {
        this.notables = notables;
    }

    public void setRequiredInternalItemNames(Set<String> requiredInternalItemNames) {
        this.requiredInternalItemNames = requiredInternalItemNames;
    }

    public void setMinimumLevel(Integer minimumLevel) {
        this.minimumLevel = minimumLevel;
    }

    public Set<String> getAreaNames() {
        return areaNames;
    }

    public void setAreaNames(Set<String> areaNames) {
        this.areaNames = areaNames;
    }

    public int getFloorId() {
        return floorId;
    }

    public void setFloorId(int floorId) {
        this.floorId = floorId;
    }

    public Set<String> getRoomTags() {
        return roomTags;
    }

    public void setRoomTags(Set<String> roomTags) {
        this.roomTags = roomTags;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public Map<String, String> getEnterExitNames() {
        return enterExitNames;
    }

    public void setEnterExitNames(Map<String, String> enterExitNames) {
        this.enterExitNames = enterExitNames;
    }

    public Map<String, String> getNotables() {
        return notables;
    }

    public static void main(String[] args) {

        RoomModel roomModel = new RoomModelBuilder().build();
        roomModel.setRoomId(1);
        roomModel.setRoomDescription("A large and empty area.");
        roomModel.setRoomTitle("The flimflam.");
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(roomModel, RoomModel.class);
        System.out.println(s);

    }
}
