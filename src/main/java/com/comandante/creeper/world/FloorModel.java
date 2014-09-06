package com.comandante.creeper.world;

import java.util.Set;

public class FloorModel {

    String name;
    Integer id;
    String rawMatrixCsv;
    Set<RoomModel> roomModels;

    public Set<RoomModel> getRoomModels() {
        return roomModels;
    }

    public void setRoomModels(Set<RoomModel> roomModels) {
        this.roomModels = roomModels;
    }

    public String getRawMatrixCsv() {
        return rawMatrixCsv;
    }

    public void setRawMatrixCsv(String rawMatrixCsv) {
        this.rawMatrixCsv = rawMatrixCsv;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
