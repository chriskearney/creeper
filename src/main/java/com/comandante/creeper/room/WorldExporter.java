package com.comandante.creeper.room;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WorldExporter {

    private final static String WORLD_DIR = "world/";

    private final RoomManager roomManager;
    private final MapsManager mapsManager;
    private final FloorManager floorManager;

    public WorldExporter(RoomManager roomManager, MapsManager mapsManager, FloorManager floorManager) {
        this.roomManager = roomManager;
        this.mapsManager = mapsManager;
        this.floorManager = floorManager;
    }

    public void saveWorld() {
        Set<Integer> floorIds = floorManager.getFloorIds();
        for (Integer floorId : floorIds) {
            writeFloor(floorId, mapsManager.getFloorMatrixMaps().get(floorId));
        }
    }

    private void writeFloor(Integer floorId, MapMatrix mapMatrix) {
        Set<Room> rooms = roomManager.getRoomsByFloorId(floorId);
        FloorModel floorModel = new FloorModel();
        floorModel.setId(floorId);
        floorModel.setRawMatrixCsv(mapMatrix.getCsv());
        floorModel.setRoomModels((new HashSet<RoomModel>()));
        Iterator<RoomModel> roomModels = Iterators.transform(rooms.iterator(), getRoomModels());
        while (roomModels.hasNext()) {
            RoomModel next = roomModels.next();
            floorModel.getRoomModels().add(next);
        }

        String floorjson = new GsonBuilder().setPrettyPrinting().create().toJson(floorModel, FloorModel.class);

        try {
            Files.write(floorjson.getBytes(), new File(WORLD_DIR + floorManager.getName(floorId) + ".json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Function<Room, RoomModel> getRoomModels() {
        return new Function<Room, RoomModel>() {
            @Override
            public RoomModel apply(Room room) {
                RoomModelBuilder roomModelBuilder = new RoomModelBuilder();
                roomModelBuilder.setRoomDescription(room.getRoomDescription());
                roomModelBuilder.setRoomTitle(room.getRoomTitle());
                roomModelBuilder.setRoomId(room.getRoomId());
                roomModelBuilder.setRoomTags(room.getRoomTags());
                return roomModelBuilder.build();
            }
        };
    }

}
