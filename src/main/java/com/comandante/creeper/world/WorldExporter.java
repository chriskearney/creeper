package com.comandante.creeper.world;

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
        WorldModel worldModel = new WorldModel();
        Set<FloorModel> floors = Sets.newHashSet();
        Set<Integer> floorIds = floorManager.getFloorIds();
        for (Integer floorId : floorIds) {
            floors.add(generateFloorModel(floorId, mapsManager.getFloorMatrixMaps().get(floorId)));
        }
        worldModel.setFloorModelList(floors);

        String worldJson = new GsonBuilder().setPrettyPrinting().create().toJson(worldModel, WorldModel.class);
        try {
            Files.write(worldJson.getBytes(), new File(WORLD_DIR + "world.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FloorModel generateFloorModel(Integer floorId, MapMatrix mapMatrix) {
        Set<Room> rooms = roomManager.getRoomsByFloorId(floorId);
        FloorModel floorModel = new FloorModel();
        floorModel.setId(floorId);
        floorModel.setRawMatrixCsv(mapMatrix.getCsv());
        floorModel.setRoomModels((new HashSet<RoomModel>()));
        floorModel.setName(floorManager.getName(floorId));
        Iterator<RoomModel> roomModels = Iterators.transform(rooms.iterator(), buildRoomModelsFromRooms());
        while (roomModels.hasNext()) {
            RoomModel next = roomModels.next();
            floorModel.getRoomModels().add(next);
        }

        return floorModel;
    }

    public static Function<Room, RoomModel> buildRoomModelsFromRooms() {
        return new Function<Room, RoomModel>() {
            @Override
            public RoomModel apply(Room room) {
                RoomModelBuilder roomModelBuilder = new RoomModelBuilder();
                roomModelBuilder.setRoomDescription(room.getRoomDescription());
                roomModelBuilder.setRoomTitle(room.getRoomTitle());
                roomModelBuilder.setRoomId(room.getRoomId());
                roomModelBuilder.setRoomTags(room.getRoomTags());
                roomModelBuilder.setFloorId(room.getFloorId());
                for (Area area : room.getAreas()) {
                    roomModelBuilder.addAreaName(area.getName());
                }
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
                for (String areaName : roomModel.getAreaNames()) {
                    basicRoomBuilder.addArea(Area.getByName(areaName));
                }
                configureExits(basicRoomBuilder, mapMatrix, roomModel.getRoomId());
                return basicRoomBuilder.createBasicRoom();
            }
        };
    }

    private void configureExits(BasicRoomBuilder basicRoomBuilder, MapMatrix mapMatrix, int roomId) {
        Integer north = mapMatrix.getNorthernExit(roomId);
        if (north > 0) {
            basicRoomBuilder.setNorthId(Optional.of(north));
        }
        Integer east = mapMatrix.getEasternExit(roomId);
        if (east > 0) {
            basicRoomBuilder.setEastId(Optional.of(east));
        }
        Integer south = mapMatrix.getSouthernExit(roomId);
        if (south > 0) {
            basicRoomBuilder.setSouthId(Optional.of(south));
        }
        Integer west = mapMatrix.getWesternExit(roomId);
        if (west > 0) {
            basicRoomBuilder.setWestId(Optional.of(west));
        }
        if (mapMatrix.getRemotes().containsKey(roomId)) {
            for (RemoteExit exit : mapMatrix.getRemotes().get(roomId)) {
                if (exit.getDirection().equals(RemoteExit.Direction.UP)) {
                    basicRoomBuilder.setUpId(Optional.of(exit.getRoomId()));
                } else if (exit.getDirection().equals(RemoteExit.Direction.DOWN)) {
                    basicRoomBuilder.setDownId(Optional.of(exit.getRoomId()));
                }
            }
        }
    }

    private void buildFloor(FloorModel floorModel) {
        MapMatrix matrixFromCsv = MapMatrix.createMatrixFromCsv(floorModel.getRawMatrixCsv());
        Set<Room> rooms = Sets.newHashSet();
        if (floorModel.getRoomModels() == null || floorModel.getRoomModels().size() == 0) {
            Iterator<List<Integer>> rows = matrixFromCsv.getRows();
            while (rows.hasNext()) {
                List<Integer> row = rows.next();
                for (Integer roomId : row) {
                    if (roomId.equals(0)) {
                        continue;
                    }
                    BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder();
                    basicRoomBuilder.setFloorId(floorModel.getId());
                    basicRoomBuilder.setRoomId(roomId);
                    basicRoomBuilder.setRoomTitle("This is a blank title.");
                    basicRoomBuilder.setRoomDescription("This is a blank Description.\nWords should go here, ideally.");
                    basicRoomBuilder.addArea(Area.DEFAULT);
                    configureExits(basicRoomBuilder, matrixFromCsv, roomId);
                    rooms.add(basicRoomBuilder.createBasicRoom());
                }
            }
            for (Room r : rooms) {
                entityManager.addEntity(r);
            }
            floorManager.addFloor(floorModel.getId(), floorModel.getName());
            mapsManager.addFloorMatrix(floorModel.getId(), matrixFromCsv);
            return;
        }
        Iterator<BasicRoom> transform = Iterators.transform(floorModel.getRoomModels().iterator(), getBasicRoom(matrixFromCsv));
        while (transform.hasNext()) {
            BasicRoom next = transform.next();
            entityManager.addEntity(next);
        }
        floorManager.addFloor(floorModel.getId(), floorModel.getName());
        mapsManager.addFloorMatrix(floorModel.getId(), matrixFromCsv);
    }

    public void readWorldFromDisk() throws FileNotFoundException {
        WorldModel worldModel = new GsonBuilder().create().fromJson(Files.newReader(new File(("world/world.json")), Charset.defaultCharset()), WorldModel.class);
        for (FloorModel next : worldModel.getFloorModelList()) {
            buildFloor(next);
        }
    }

}
