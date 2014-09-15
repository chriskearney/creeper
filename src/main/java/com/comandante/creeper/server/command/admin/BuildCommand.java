package com.comandante.creeper.server.command.admin;

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
import com.comandante.creeper.world.FloorModel;
import com.comandante.creeper.world.MapMatrix;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.world.RoomManager;
import com.comandante.creeper.world.RoomModel;
import com.comandante.creeper.world.WorldExporter;
import com.google.common.base.Optional;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
            //
            CreeperSession session = extractCreeperSession(e.getChannel());
            String playerId = extractPlayerId(session);
            GameManager gameManager = getGameManager();
            Player player = gameManager.getPlayerManager().getPlayer(playerId);
            Room currentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
            ChannelUtils utils = gameManager.getChannelUtils();
            MapsManager mapsManager = gameManager.getMapsManager();
            MapMatrix mapMatrix = mapsManager.getFloorMatrixMaps().get(currentRoom.getFloorId());
            Coords roomCoords = mapMatrix.getCoords(currentRoom.getRoomId());
            List<String> originalMessageParts = getOriginalMessageParts(e);
            if (originalMessageParts.size() == 1) {
                utils.write(playerId, "You must specify a direction in which to build.");
                return;
            }
            String desiredBuildDirection = originalMessageParts.get(1);
            if (desiredBuildDirection.equalsIgnoreCase("n") | desiredBuildDirection.equalsIgnoreCase("north")) {
                if (!currentRoom.getNorthId().isPresent() && mapMatrix.getNorth(currentRoom.getRoomId()) == 0) {
                    Coords coords = new Coords(roomCoords.row - 1, roomCoords.column);
                    if (coords.getRow() < 0) {
                        mapMatrix.addRow(true);
                        coords = new Coords(0, roomCoords.column);
                    }
                    buildBasicRoom(player, currentRoom, coords, mapMatrix);
                    utils.write(playerId, "Room created.");
                    return;
                } else {
                    utils.write(playerId, "Error!  There is already a room to the North.");
                }
            } else if (desiredBuildDirection.equalsIgnoreCase("s") | desiredBuildDirection.equalsIgnoreCase("south")) {
                if (!currentRoom.getSouthId().isPresent() && mapMatrix.getSouth(currentRoom.getRoomId()) == 0) {
                    Coords coords = new Coords(roomCoords.row + 1, roomCoords.column);
                    if (coords.getRow() >= mapMatrix.getMaxRow()) {
                        mapMatrix.addRow(false);
                    }
                    buildBasicRoom(player, currentRoom, coords, mapMatrix);
                    utils.write(playerId, "Room created.");
                    return;
                } else {
                    utils.write(playerId, "Error!  There is already a room to the South.");
                }
            } else if (desiredBuildDirection.equalsIgnoreCase("e") | desiredBuildDirection.equalsIgnoreCase("east")) {
                if (!currentRoom.getEastId().isPresent() && mapMatrix.getEast(currentRoom.getRoomId()) == 0) {
                    Coords coords = new Coords(roomCoords.row, roomCoords.column + 1);
                    if (coords.getColumn() >= mapMatrix.getMaxCol()) {
                        mapMatrix.addColumn(false);
                        coords = new Coords(roomCoords.row, roomCoords.column + 1);
                    }
                    buildBasicRoom(player, currentRoom, coords, mapMatrix);
                    utils.write(playerId, "Room created.");
                    return;
                } else {
                    utils.write(playerId, "Error!  There is already a room to the East.");
                }
            } else if (desiredBuildDirection.equalsIgnoreCase("w") | desiredBuildDirection.equalsIgnoreCase("west")) {
                if (!currentRoom.getWestId().isPresent() && mapMatrix.getWest(currentRoom.getRoomId()) == 0) {
                    Coords coords = new Coords(roomCoords.row, roomCoords.column - 1);
                    if (coords.getColumn() < 0) {
                        mapMatrix.addColumn(true);
                        coords = new Coords(roomCoords.row, 0);
                    }
                    buildBasicRoom(player, currentRoom, coords, mapMatrix);
                    utils.write(playerId, "Room created.");
                    return;
                } else {
                    utils.write(playerId, "Error!  There is already a room to the West.");
                }
            } else if (desiredBuildDirection.equalsIgnoreCase("u") | desiredBuildDirection.equalsIgnoreCase("up")) {
                Integer newRoomId = findRoomId();
                Integer floorId = findFloorId();
                FloorModel floorModel = new FloorModel();
                floorModel.setId(floorId);
                floorModel.setRawMatrixCsv(Integer.toString(newRoomId));
                floorModel.setName(UUID.randomUUID().toString());
                BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder();
                basicRoomBuilder.addArea(Area.DEFAULT);
                basicRoomBuilder.setRoomId(newRoomId);
                basicRoomBuilder.setRoomDescription("Newly created room. Set a new description with the desc command.");
                basicRoomBuilder.setRoomTitle("Default Title, change with title command");
                basicRoomBuilder.setFloorId(floorId);
                basicRoomBuilder.setDownId(Optional.of(currentRoom.getRoomId()));
                currentRoom.setUpId(Optional.of(newRoomId));
                BasicRoom basicRoom = basicRoomBuilder.createBasicRoom();
                getGameManager().getEntityManager().addEntity(basicRoom);
                Iterator<RoomModel> transform = Iterators.transform(Sets.newHashSet(basicRoom).iterator(), WorldExporter.getRoomModels());
                floorModel.setRoomModels(Sets.newHashSet(transform));
                getGameManager().getFloorManager().addFloor(floorModel.getId(), floorModel.getName());
                getGameManager().getMapsManager().addFloorMatrix(floorModel.getId(), MapMatrix.createMatrixFromCsv(floorModel.getRawMatrixCsv()));
                getGameManager().getMapsManager().generateAllMaps(9, 9);
               // getGameManager().getMapsManager().drawMap(basicRoom.getRoomId(), new Coords(9, 9));
                getGameManager().movePlayer(new PlayerMovement(player, currentRoom.getRoomId(), basicRoom.getRoomId(), null, "", ""));
                getGameManager().currentRoomLogic(player.getPlayerId());
                return;
            } else {
                utils.write(playerId, "Error!  There is already a room to the West.");
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    private void buildBasicRoom(Player player, Room currentRoom, Coords newCords, MapMatrix mapMatrix) {
        Integer newRroomId = findRoomId();
        mapMatrix.setCoordsValue(newCords, newRroomId);
        BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder();
        basicRoomBuilder.addArea(Area.DEFAULT);
        basicRoomBuilder.setRoomId(newRroomId);
        basicRoomBuilder.setRoomDescription("Newly created room. Set a new description with the desc command.");
        basicRoomBuilder.setRoomTitle("Default Title, change with title command");
        basicRoomBuilder.setFloorId(currentRoom.getFloorId());
        BasicRoom basicRoom = basicRoomBuilder.createBasicRoom();
        getGameManager().getRoomManager().addRoom(basicRoom);
        getGameManager().getEntityManager().addEntity(basicRoom);
        rebuildExits(basicRoom, mapMatrix);
        rebuildExits(currentRoom, mapMatrix);
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
        getGameManager().getMapsManager().generateAllMaps(9, 9);
        getGameManager().movePlayer(new PlayerMovement(player, currentRoom.getRoomId(), basicRoom.getRoomId(), null, "", ""));
        getGameManager().currentRoomLogic(player.getPlayerId());
    }

    private void rebuildExits(Room room, MapMatrix mapMatrix) {
        if (mapMatrix.getNorth(room.getRoomId()) > 0) {
            room.setNorthId(Optional.of(mapMatrix.getNorth(room.getRoomId())));
        }
        if (mapMatrix.getSouth(room.getRoomId()) > 0) {
            room.setSouthId(Optional.of(mapMatrix.getSouth(room.getRoomId())));
        }
        if (mapMatrix.getEast(room.getRoomId()) > 0) {
            room.setEastId(Optional.of(mapMatrix.getEast(room.getRoomId())));
        }
        if (mapMatrix.getWest(room.getRoomId()) > 0) {
            room.setWestId(Optional.of(mapMatrix.getWest(room.getRoomId())));
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
