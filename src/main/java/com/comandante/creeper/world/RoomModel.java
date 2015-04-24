package com.comandante.creeper.world;

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

    public RoomModel(int roomId, int floorId, String roomDescription, String roomTitle, Set<String> roomTags, Set<String> areaNames, Map<String, String> enterExitNames) {
        this.roomId = roomId;
        this.floorId = floorId;
        this.roomDescription = roomDescription;
        this.roomTitle = roomTitle;
        this.roomTags = roomTags;
        this.areaNames = areaNames;
        this.enterExitNames = enterExitNames;
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

    public static void main(String[] args) {

        RoomModel roomModel = new RoomModelBuilder().build();
        roomModel.setRoomId(1);
        roomModel.setRoomDescription("A large and empty area.");
        roomModel.setRoomTitle("The flimflam.");
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(roomModel, RoomModel.class);
        System.out.println(s);

    }
}
