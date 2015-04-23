package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.server.command.Command;
import com.comandante.creeper.world.Area;
import com.comandante.creeper.world.BasicRoom;
import com.comandante.creeper.world.BasicRoomBuilder;
import com.comandante.creeper.world.Coords;
import com.comandante.creeper.world.FloorModel;
import com.comandante.creeper.world.MapMatrix;
import com.comandante.creeper.world.RemoteExit;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.world.WorldExporter;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BuildCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("build", "b");
    final static String description = "Build new rooms in the world.";
    final static boolean isAdminOnly = true;

    public BuildCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public synchronized void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (originalMessageParts.size() > 1) {
                String desiredBuildDirection = originalMessageParts.get(1);
                if (desiredBuildDirection.equalsIgnoreCase("n") | desiredBuildDirection.equalsIgnoreCase("north")) {
                    if (!currentRoom.getNorthId().isPresent() && mapMatrix.isNorthernMapSpaceEmpty(currentRoom.getRoomId())) {
                        buildBasicRoomAndProcessAllExits(mapMatrix.getNorthCords(currentRoomCoords));
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("s") | desiredBuildDirection.equalsIgnoreCase("south")) {
                    if (!currentRoom.getSouthId().isPresent() && mapMatrix.isSouthernMapSpaceEmpty(currentRoom.getRoomId())) {
                        buildBasicRoomAndProcessAllExits(mapMatrix.getSouthCoords(currentRoomCoords));
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("e") | desiredBuildDirection.equalsIgnoreCase("east")) {
                    if (!currentRoom.getEastId().isPresent() && mapMatrix.isEasternMapSpaceEmpty(currentRoom.getRoomId())) {
                        buildBasicRoomAndProcessAllExits(mapMatrix.getEastCoords(currentRoomCoords));
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("w") | desiredBuildDirection.equalsIgnoreCase("west")) {
                    if (!currentRoom.getWestId().isPresent() && mapMatrix.isWesternMapSpaceEmpty(currentRoom.getRoomId())) {
                        buildBasicRoomAndProcessAllExits(mapMatrix.getWestCoords(currentRoomCoords));
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("u") | desiredBuildDirection.equalsIgnoreCase("up")) {
                    if (!currentRoom.getUpId().isPresent()) {
                        Integer newRoomId = findUnusedRoomId();
                        Integer newFloorId = findUnusedFloorId();
                        RemoteExit remoteExit = new RemoteExit(RemoteExit.Direction.UP, newRoomId, "");
                        RemoteExit returnRemoteExit = new RemoteExit(RemoteExit.Direction.DOWN, currentRoom.getRoomId(), "");
                        mapMatrix.addRemote(currentRoom.getRoomId(), remoteExit);
                        FloorModel newFloorModel = newFloorModel(newFloorId, newRoomId, currentRoom.getRoomId(), returnRemoteExit);
                        BasicRoom basicRoom = newBasic()
                                .setRoomId(newRoomId)
                                .setFloorId(newFloorId)
                                .setDownId(Optional.of(currentRoom.getRoomId()))
                                .createBasicRoom();
                        currentRoom.setUpId(Optional.of(newRoomId));
                        entityManager.addEntity(basicRoom);
                        newFloorModel.setRoomModels(Sets.newHashSet(Iterators.transform(Sets.newHashSet(basicRoom).iterator(), WorldExporter.buildRoomModelsFromRooms())));
                        floorManager.addFloor(newFloorModel.getId(), newFloorModel.getName());
                        mapsManager.addFloorMatrix(newFloorModel.getId(), MapMatrix.createMatrixFromCsv(newFloorModel.getRawMatrixCsv()));
                        mapsManager.generateAllMaps(9, 9);
                        gameManager.movePlayer(new PlayerMovement(player, currentRoom.getRoomId(), basicRoom.getRoomId(), null, "", ""));
                        gameManager.currentRoomLogic(player.getPlayerId());
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("d") | desiredBuildDirection.equalsIgnoreCase("down")) {
                    if (!currentRoom.getDownId().isPresent()) {
                        Integer newRoomId = findUnusedRoomId();
                        Integer newFloorId = findUnusedFloorId();
                        RemoteExit remoteExit = new RemoteExit(RemoteExit.Direction.DOWN, newRoomId, "");
                        RemoteExit returnRemoteExit = new RemoteExit(RemoteExit.Direction.UP, currentRoom.getRoomId(), "");
                        mapMatrix.addRemote(currentRoom.getRoomId(), remoteExit);
                        FloorModel newFloorModel = newFloorModel(newFloorId, newRoomId, currentRoom.getRoomId(), returnRemoteExit);
                        BasicRoom basicRoom = newBasic()
                                .setRoomId(newRoomId)
                                .setFloorId(newFloorId)
                                .setUpId(Optional.of(currentRoom.getRoomId()))
                                .createBasicRoom();
                        currentRoom.setDownId(Optional.of(newRoomId));
                        entityManager.addEntity(basicRoom);
                        newFloorModel.setRoomModels(Sets.newHashSet(Iterators.transform(Sets.newHashSet(basicRoom).iterator(), WorldExporter.buildRoomModelsFromRooms())));
                        floorManager.addFloor(newFloorModel.getId(), newFloorModel.getName());
                        mapsManager.addFloorMatrix(newFloorModel.getId(), MapMatrix.createMatrixFromCsv(newFloorModel.getRawMatrixCsv()));
                        mapsManager.generateAllMaps(9, 9);
                        gameManager.movePlayer(new PlayerMovement(player, currentRoom.getRoomId(), basicRoom.getRoomId(), null, "", ""));
                        gameManager.currentRoomLogic(player.getPlayerId());
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("enter")) {
                    String enterName = originalMessageParts.get(2);
                    Integer newRoomId = findUnusedRoomId();
                    Integer newFloorId = findUnusedFloorId();
                    RemoteExit remoteExit = new RemoteExit(RemoteExit.Direction.ENTER, newRoomId, enterName);
                    RemoteExit returnRemoteExit = new RemoteExit(RemoteExit.Direction.ENTER, currentRoom.getRoomId(), "Leave");
                    mapMatrix.addRemote(currentRoom.getRoomId(),remoteExit );
                    FloorModel newFloorModel = newFloorModel(newFloorId, newRoomId, currentRoom.getRoomId(), returnRemoteExit);
                    BasicRoom basicRoom = newBasic()
                            .setRoomId(newRoomId)
                            .setFloorId(newFloorId)
                            .addEnterExit(returnRemoteExit)
                            .createBasicRoom();
                    currentRoom.addEnterExit(remoteExit);
                    entityManager.addEntity(basicRoom);
                    newFloorModel.setRoomModels(Sets.newHashSet(Iterators.transform(Sets.newHashSet(basicRoom).iterator(), WorldExporter.buildRoomModelsFromRooms())));
                    floorManager.addFloor(newFloorModel.getId(), newFloorModel.getName());
                    mapsManager.addFloorMatrix(newFloorModel.getId(), MapMatrix.createMatrixFromCsv(newFloorModel.getRawMatrixCsv()));
                    mapsManager.generateAllMaps(9, 9);
                    gameManager.movePlayer(new PlayerMovement(player, currentRoom.getRoomId(), basicRoom.getRoomId(), null, "", ""));
                    gameManager.currentRoomLogic(player.getPlayerId());
                    return;
                }
                channelUtils.write(playerId, "Room already exists at that location.");
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    private FloorModel newFloorModel(Integer floorId, Integer newRoomId, Integer currentRoomId, RemoteExit remoteExit) {
        FloorModel newFloorModel = new FloorModel();
        RemoteExit.Direction returnDirection = remoteExit.getDirection();
        newFloorModel.setId(floorId);
        if (returnDirection.equals(RemoteExit.Direction.DOWN)) {
            newFloorModel.setRawMatrixCsv(Integer.toString(newRoomId) + "d|" + currentRoomId);
        } else if (returnDirection.equals(RemoteExit.Direction.UP)) {
            newFloorModel.setRawMatrixCsv(Integer.toString(newRoomId) + "u|" + currentRoomId);
        } else if (returnDirection.equals(RemoteExit.Direction.ENTER)) {
            newFloorModel.setRawMatrixCsv(Integer.toString(newRoomId) + "e|" + currentRoomId + "ee" + remoteExit.getExitDetail());
        }
        newFloorModel.setName(UUID.randomUUID().toString());
        return newFloorModel;
    }

    private BasicRoomBuilder newBasic() {
        BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder();
        basicRoomBuilder.addArea(Area.DEFAULT);
        basicRoomBuilder.setRoomDescription("Newly created room. Set a new description with the desc command.");
        basicRoomBuilder.setRoomTitle("Default Title, change with title command");
        return basicRoomBuilder;
    }

    private void buildBasicRoomAndProcessAllExits(Coords newCords) {
        Integer newRroomId = findUnusedRoomId();
        mapMatrix.setCoordsValue(newCords, newRroomId);
        BasicRoom basicRoom = newBasic()
                .setRoomId(newRroomId)
                .setFloorId(currentRoom.getFloorId())
                .createBasicRoom();
        roomManager.addRoom(basicRoom);
        entityManager.addEntity(basicRoom);
        rebuildExits(basicRoom, mapMatrix);
        rebuildExits(currentRoom, mapMatrix);
        processExits(basicRoom, mapMatrix);
        mapsManager.generateAllMaps(9, 9);
        gameManager.movePlayer(new PlayerMovement(player, currentRoom.getRoomId(), basicRoom.getRoomId(), null, "", ""));
        gameManager.currentRoomLogic(player.getPlayerId());
        write("Room Created.");
    }

    private void processExits(BasicRoom basicRoom, MapMatrix mapMatrix) {
        if (basicRoom.getNorthId().isPresent()) {
            rebuildExits(roomManager.getRoom(basicRoom.getNorthId().get()), mapMatrix);
        }
        if (basicRoom.getSouthId().isPresent()) {
            rebuildExits(roomManager.getRoom(basicRoom.getSouthId().get()), mapMatrix);
        }
        if (basicRoom.getEastId().isPresent()) {
            rebuildExits(roomManager.getRoom(basicRoom.getEastId().get()), mapMatrix);
        }
        if (basicRoom.getWestId().isPresent()) {
            rebuildExits(roomManager.getRoom(basicRoom.getWestId().get()), mapMatrix);
        }
    }

    private void rebuildExits(Room room, MapMatrix mapMatrix) {
        if (mapMatrix.getNorthernExit(room.getRoomId()) > 0) {
            room.setNorthId(Optional.of(mapMatrix.getNorthernExit(room.getRoomId())));
        }
        if (mapMatrix.getSouthernExit(room.getRoomId()) > 0) {
            room.setSouthId(Optional.of(mapMatrix.getSouthernExit(room.getRoomId())));
        }
        if (mapMatrix.getEasternExit(room.getRoomId()) > 0) {
            room.setEastId(Optional.of(mapMatrix.getEasternExit(room.getRoomId())));
        }
        if (mapMatrix.getWesternExit(room.getRoomId()) > 0) {
            room.setWestId(Optional.of(mapMatrix.getWesternExit(room.getRoomId())));
        }
        room.setEnterExits(Lists.<RemoteExit>newArrayList());
        if (mapMatrix.getRemotes().containsKey(room.getRoomId())) {
            Set<RemoteExit> remoteExits = mapMatrix.getRemotes().get(room.getRoomId());
            for (RemoteExit next : remoteExits) {
                if (next.getDirection().equals(RemoteExit.Direction.UP)) {
                    room.setUpId(Optional.of(next.getRoomId()));
                } else if (next.getDirection().equals(RemoteExit.Direction.DOWN)) {
                    room.setDownId(Optional.of(next.getRoomId()));
                } else if (next.getDirection().equals(RemoteExit.Direction.ENTER)) {
                    room.addEnterExit(next);
                }
            }
        }
    }

    private synchronized Integer findUnusedRoomId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!roomManager.doesRoomIdExist(i)) {
                return i;
            }
        }
        return 0;
    }

    private synchronized Integer findUnusedFloorId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!floorManager.doesFloorIdExist(i)) {
                return i;
            }
        }
        return 0;
    }
}
