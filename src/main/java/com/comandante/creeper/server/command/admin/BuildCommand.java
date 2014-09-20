package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.CreeperSession;
import com.comandante.creeper.server.command.Command;
import com.comandante.creeper.world.Area;
import com.comandante.creeper.world.BasicRoom;
import com.comandante.creeper.world.BasicRoomBuilder;
import com.comandante.creeper.world.Coords;
import com.comandante.creeper.world.FloorManager;
import com.comandante.creeper.world.FloorModel;
import com.comandante.creeper.world.MapMatrix;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.RemoteExit;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.world.RoomManager;
import com.comandante.creeper.world.WorldExporter;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
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
        try {

            CreeperSession session = extractCreeperSession(e.getChannel());
            String playerId = extractPlayerId(session);
            GameManager gameManager = getGameManager();
            Player player = gameManager.getPlayerManager().getPlayer(playerId);
            Room currentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
            ChannelUtils utils = gameManager.getChannelUtils();
            MapsManager mapsManager = gameManager.getMapsManager();
            MapMatrix mapMatrix = mapsManager.getFloorMatrixMaps().get(currentRoom.getFloorId());
            Coords currentRoomCoords = mapMatrix.getCoords(currentRoom.getRoomId());
            EntityManager entityManager = gameManager.getEntityManager();
            FloorManager floorManager = gameManager.getFloorManager();

            if (getOriginalMessageParts(e).size() > 1) {
                String desiredBuildDirection = getOriginalMessageParts(e).get(1);
                if (desiredBuildDirection.equalsIgnoreCase("n") | desiredBuildDirection.equalsIgnoreCase("north")) {
                    if (!currentRoom.getNorthId().isPresent() && mapMatrix.isNorthernMapSpaceEmpty(currentRoom.getRoomId())) {
                        buildBasicRoomAndProcessAllExits(player, currentRoom, mapMatrix.getNorthCords(currentRoomCoords), mapMatrix);
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("s") | desiredBuildDirection.equalsIgnoreCase("south")) {
                    if (!currentRoom.getSouthId().isPresent() && mapMatrix.isSouthernMapSpaceEmpty(currentRoom.getRoomId())) {
                        buildBasicRoomAndProcessAllExits(player, currentRoom, mapMatrix.getSouthCoords(currentRoomCoords), mapMatrix);
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("e") | desiredBuildDirection.equalsIgnoreCase("east")) {
                    if (!currentRoom.getEastId().isPresent() && mapMatrix.isEasternMapSpaceEmpty(currentRoom.getRoomId())) {
                        buildBasicRoomAndProcessAllExits(player, currentRoom, mapMatrix.getEastCoords(currentRoomCoords), mapMatrix);
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("w") | desiredBuildDirection.equalsIgnoreCase("west")) {
                    if (!currentRoom.getWestId().isPresent() && mapMatrix.isWesternMapSpaceEmpty(currentRoom.getRoomId())) {
                        buildBasicRoomAndProcessAllExits(player, currentRoom, mapMatrix.getSouthCoords(currentRoomCoords), mapMatrix);
                        return;
                    }
                } else if (desiredBuildDirection.equalsIgnoreCase("u") | desiredBuildDirection.equalsIgnoreCase("up")) {
                    if (!currentRoom.getUpId().isPresent()) {
                        Integer newRoomId = findRoomId();
                        Integer newFloorId = findFloorId();
                        mapMatrix.addRemote(currentRoom.getRoomId(), new RemoteExit(RemoteExit.Direction.UP, newRoomId));
                        FloorModel newFloorModel = newFloorModel(newFloorId, newRoomId, currentRoom.getRoomId(), RemoteExit.Direction.DOWN);
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
                        Integer newRoomId = findRoomId();
                        Integer newFloorId = findFloorId();
                        mapMatrix.addRemote(currentRoom.getRoomId(), new RemoteExit(RemoteExit.Direction.DOWN, newRoomId));
                        FloorModel newFloorModel = newFloorModel(newFloorId, newRoomId, currentRoom.getRoomId(), RemoteExit.Direction.UP);
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
                }
                utils.write(playerId, "Room already exists at that location.");
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    private FloorModel newFloorModel(Integer floorId, Integer newRoomId, Integer currentRoomId, RemoteExit.Direction returnDirection) {
        FloorModel newFloorModel = new FloorModel();
        newFloorModel.setId(floorId);
        if (returnDirection.equals(RemoteExit.Direction.DOWN)) {
            newFloorModel.setRawMatrixCsv(Integer.toString(newRoomId) + "d" + currentRoomId);
        } else if (returnDirection.equals(RemoteExit.Direction.UP)) {
            newFloorModel.setRawMatrixCsv(Integer.toString(newRoomId) + "u" + currentRoomId);
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

    private void buildBasicRoomAndProcessAllExits(Player player, Room currentRoom, Coords newCords, MapMatrix mapMatrix) {
        Integer newRroomId = findRoomId();
        mapMatrix.setCoordsValue(newCords, newRroomId);
        BasicRoom basicRoom = newBasic()
                .setRoomId(newRroomId)
                .setFloorId(currentRoom.getFloorId())
                .createBasicRoom();
        getGameManager().getEntityManager().addEntity(basicRoom);
        rebuildExits(basicRoom, mapMatrix);
        rebuildExits(currentRoom, mapMatrix);
        processExits(basicRoom, mapMatrix);
        getGameManager().getMapsManager().generateAllMaps(9, 9);
        getGameManager().movePlayer(new PlayerMovement(player, currentRoom.getRoomId(), basicRoom.getRoomId(), null, "", ""));
        getGameManager().currentRoomLogic(player.getPlayerId());
        getGameManager().getChannelUtils().write(player.getPlayerId(), "Room Created.");
    }

    private void processExits(BasicRoom basicRoom, MapMatrix mapMatrix) {
        RoomManager roomManager = getGameManager().getRoomManager();
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
        if (mapMatrix.getRemotes().containsKey(room.getRoomId())) {
            Set<RemoteExit> remoteExits = mapMatrix.getRemotes().get(room.getRoomId());
            for (RemoteExit next : remoteExits) {
                if (next.getDirection().equals(RemoteExit.Direction.UP)) {
                    room.setUpId(Optional.of(next.getRoomId()));
                } else if (next.getDirection().equals(RemoteExit.Direction.DOWN)) {
                    room.setDownId(Optional.of(next.getRoomId()));
                }
            }
        }
    }

    private synchronized Integer findRoomId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!getGameManager().getRoomManager().doesRoomIdExist(i)) {
                return i;
            }
        }
        return 0;
    }


    private synchronized Integer findFloorId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!getGameManager().getFloorManager().doesFloorIdExist(i)) {
                return i;
            }
        }
        return 0;
    }
}
