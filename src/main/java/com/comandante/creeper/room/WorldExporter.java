package com.comandante.creeper.room;

import com.comandante.creeper.entity.EntityManager;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class WorldExporter {

    private final static String WORLD_DIR = "world/";

    private final RoomManager roomManager;
    private final MapsManager mapsManager;
    private final FloorManager floorManager;
    private final EntityManager entityManager;

    public WorldExporter(RoomManager roomManager, MapsManager mapsManager, FloorManager floorManager, EntityManager entityManager) {
        this.roomManager = roomManager;
        this.mapsManager = mapsManager;
        this.floorManager = floorManager;
        this.entityManager = entityManager;
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
        floorModel.setName(floorManager.getName(floorId));
        Iterator<RoomModel> roomModels = Iterators.transform(rooms.iterator(), getRoomModels());
        while (roomModels.hasNext()) {
            RoomModel next = roomModels.next();
            floorModel.getRoomModels().add(next);
        }

        String floorjson = new GsonBuilder().setPrettyPrinting().create().toJson(floorModel, FloorModel.class);

        try {
            Files.write(floorjson.getBytes(), new File(WORLD_DIR + floorManager.getName(floorId) + "_floor.json"));
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

    public Function<RoomModel, BasicRoom> getBasicRoom(final MapMatrix mapMatrix) {
        return new Function<RoomModel, BasicRoom>() {
            @Override
            public BasicRoom apply(RoomModel roomModel) {
                BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder()
                        .setRoomId(roomModel.getRoomId())
                        .setFloorId(roomModel.getFloorId())
                        .setRoomDescription(roomModel.getRoomDescription())
                        .setRoomTitle(roomModel.getRoomTitle());

                for (String tag : roomModel.getRoomTags()) {
                    basicRoomBuilder.addTag(tag);
                }
                Integer north = mapMatrix.getNorth(roomModel.getRoomId());
                if (north > 0) {
                    basicRoomBuilder.setNorthId(Optional.of(north));
                }
                Integer east = mapMatrix.getEast(roomModel.getRoomId());
                if (east > 0) {
                    basicRoomBuilder.setEastId(Optional.of(east));
                }
                Integer south = mapMatrix.getSouth(roomModel.getRoomId());
                if (south > 0) {
                    basicRoomBuilder.setSouthId(Optional.of(south));
                }
                Integer west = mapMatrix.getWest(roomModel.getRoomId());
                if (west > 0) {
                    basicRoomBuilder.setWestId(Optional.of(west));
                }
                return basicRoomBuilder.createBasicRoom();
            }
        };
    }

    public MapMatrix readWorldFromDisk() throws FileNotFoundException {
        FloorModel floorModel = new GsonBuilder().create().fromJson(Files.newReader(new File(("world/main_floor.json")), Charset.defaultCharset()), FloorModel.class);
        MapMatrix matrixFromCsv = MapMatrix.createMatrixFromCsv(floorModel.getRawMatrixCsv());
        Set<Room> rooms = Sets.newHashSet();
        if (floorModel.getRoomModels().size() == 0) {
            Iterator<List<Integer>> rows = matrixFromCsv.getRows();
            while (rows.hasNext()) {
                List<Integer> row = rows.next();
                for (Integer roomId: row) {
                    if (roomId.equals(0)){
                        continue;
                    }
                    BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder();
                    basicRoomBuilder.setFloorId(floorModel.getId());
                    basicRoomBuilder.setRoomId(roomId);
                    basicRoomBuilder.setRoomTitle("This is a blank title.");
                    basicRoomBuilder.setRoomDescription("This is a blank Description.\nWords should go here, ideally.");
                    Integer north = matrixFromCsv.getNorth(roomId);
                    if (north > 0) {
                        basicRoomBuilder.setNorthId(Optional.of(north));
                    }
                    Integer east = matrixFromCsv.getEast(roomId);
                    if (east > 0) {
                        basicRoomBuilder.setEastId(Optional.of(east));
                    }
                    Integer south = matrixFromCsv.getSouth(roomId);
                    if (south > 0) {
                        basicRoomBuilder.setSouthId(Optional.of(south));
                    }
                    Integer west = matrixFromCsv.getWest(roomId);
                    if (west > 0) {
                        basicRoomBuilder.setWestId(Optional.of(west));
                    }
                    rooms.add(basicRoomBuilder.createBasicRoom());
                }
            }
            for (Room r: rooms) {
                entityManager.addEntity(r);
            }
            floorManager.addFloor(floorModel.getId(), floorModel.getName());
            mapsManager.addFloorMatrix(floorModel.getId(), matrixFromCsv);
            return matrixFromCsv;
        }
        Iterator<BasicRoom> transform = Iterators.transform(floorModel.getRoomModels().iterator(), getBasicRoom(matrixFromCsv));
        while (transform.hasNext()) {
            BasicRoom next = transform.next();
            entityManager.addEntity(next);
        }
        floorManager.addFloor(floorModel.getId(), floorModel.getName());
        mapsManager.addFloorMatrix(floorModel.getId(), matrixFromCsv);
        return matrixFromCsv;
    }

}
