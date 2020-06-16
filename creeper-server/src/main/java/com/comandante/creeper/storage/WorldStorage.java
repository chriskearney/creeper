package com.comandante.creeper.storage;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SentryManager;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.world.FloorManager;
import com.comandante.creeper.world.MapMatrix;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.RoomManager;
import com.comandante.creeper.world.model.*;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;

public class WorldStorage {

    private final static String WORLD_DIR = "world/";

    private final RoomManager roomManager;
    private final MapsManager mapsManager;
    private final FloorManager floorManager;
    private final EntityManager entityManager;
    private final GameManager gameManager;

    public WorldStorage(RoomManager roomManager, MapsManager mapsManager, FloorManager floorManager, EntityManager entityManager, GameManager gameManager) {
        this.roomManager = roomManager;
        this.mapsManager = mapsManager;
        this.floorManager = floorManager;
        this.entityManager = entityManager;
        this.gameManager = gameManager;
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
            SentryManager.logSentry(this.getClass(), e, "Save world problem!");
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
        rooms.stream()
                .map(buildRoomModelsFromRooms())
                .forEach(roomModel -> floorModel.getRoomModels()
                        .add(roomModel));
        return floorModel;
    }

    public static Function<Room, RoomModel> buildRoomModelsFromRooms() {
        return room -> {
            RoomModelBuilder roomModelBuilder = new RoomModelBuilder();
            for (RemoteExit remoteExit : room.getEnterExits()) {
                roomModelBuilder.addEnterExitName(remoteExit.getRoomId(), remoteExit.getExitDetail());
            }
            roomModelBuilder.setRoomDescription(room.getRoomDescription());
            roomModelBuilder.setRoomTitle(room.getRoomTitle());
            roomModelBuilder.setRoomId(room.getRoomId());
            roomModelBuilder.setRoomTags(room.getRoomTags());
            roomModelBuilder.setFloorId(room.getFloorId());
            for (Area area : room.getAreas()) {
                roomModelBuilder.addAreaName(area.getName());
            }
            for (Map.Entry<String, String> notable : room.getNotables().entrySet()) {
                roomModelBuilder.addNotable(notable.getKey(), notable.getValue());
            }

            if (room.getMinimumLevel().isPresent()) {
                roomModelBuilder.setMinimumLevel(room.getMinimumLevel().get());
            }
            room.getRequiredInternalItemNames().forEach(roomModelBuilder::addInternalItemName);

            return roomModelBuilder.build();
        };
    }

    public Function<RoomModel, BasicRoom> getBasicRoom(final MapMatrix mapMatrix) {
        return roomModel -> {
            BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder(gameManager)
                    .setRoomId(roomModel.getRoomId())
                    .setFloorId(roomModel.getFloorId())
                    .setRoomDescription(roomModel.getRoomDescription())
                    .setRoomTitle(roomModel.getRoomTitle());

            for (String tag : roomModel.getRoomTags()) {
                basicRoomBuilder.addTag(tag);
            }
            for (String areaName : roomModel.getAreaNames()) {
                Area byName = Area.getByName(areaName);
                if (byName != null) {
                    basicRoomBuilder.addArea(byName);
                }
            }
            Map<String, String> enterExitNames = roomModel.getEnterExitNames();
            if (enterExitNames != null) {
                for (Map.Entry<String, String> next : enterExitNames.entrySet()) {
                    RemoteExit remoteExit = new RemoteExit(RemoteExit.Direction.ENTER, Integer.parseInt(next.getKey()), next.getValue());
                    basicRoomBuilder.addEnterExit(remoteExit);
                    mapMatrix.addRemote(roomModel.getRoomId(), remoteExit);
                }
            }
            Map<String, String> notables = roomModel.getNotables();
            if (notables != null) {
                for (Map.Entry<String, String> next : notables.entrySet()) {
                    basicRoomBuilder.addNotable(next.getKey(), next.getValue());
                }
            }

            if (roomModel.getRequiredInternalItemNames() != null) {
                roomModel.getRequiredInternalItemNames().forEach(basicRoomBuilder::requiredInternalItemName);
            }

            basicRoomBuilder.minimumLevel(Optional.ofNullable(roomModel.getMinimumLevel()));

            configureExits(basicRoomBuilder, mapMatrix, roomModel.getRoomId());
            return basicRoomBuilder.createBasicRoom();
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
                    BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder(gameManager);
                    basicRoomBuilder.setFloorId(floorModel.getId());
                    basicRoomBuilder.setRoomId(roomId);
                    basicRoomBuilder.setRoomTitle("This is a blank title.");
                    basicRoomBuilder.setRoomDescription("This is a blank Description.\nWords should go here, ideally.");
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
        floorModel.getRoomModels().stream().map(getBasicRoom(matrixFromCsv)).forEach(entityManager::addEntity);
        floorManager.addFloor(floorModel.getId(), floorModel.getName());
        mapsManager.addFloorMatrix(floorModel.getId(), matrixFromCsv);
    }

    public void readWorldFromDisk() throws FileNotFoundException {
        WorldModel worldModel = new GsonBuilder().create().fromJson(Files.newReader(new File(("world/world.json")), Charset.defaultCharset()), WorldModel.class);
        worldModel.getFloorModelList()
                .forEach(this::buildFloor);
    }

    public void buildTestworld() {
        WorldModel worldModel = new GsonBuilder().create().fromJson("{\n" +
                "  \"floorModelList\": [\n" +
                "    {\n" +
                "      \"name\": \"main\",\n" +
                "      \"id\": 0,\n" +
                "      \"rawMatrixCsv\": \"0,1\"\n" +
                "}]\n" +
                "}", WorldModel.class);
        for (FloorModel next : worldModel.getFloorModelList()) {
            buildFloor(next);
        }
    }

}
