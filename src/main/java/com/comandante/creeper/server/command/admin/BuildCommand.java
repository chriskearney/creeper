package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.CreeperSession;
import com.comandante.creeper.server.command.Command;
import com.comandante.creeper.world.Area;
import com.comandante.creeper.world.BasicRoom;
import com.comandante.creeper.world.BasicRoomBuilder;
import com.comandante.creeper.world.Coords;
import com.comandante.creeper.world.MapMatrix;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.world.RoomManager;
import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class BuildCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("build", "b");
    final static String description = "Saves the current world to disk.";
    final static boolean isAdminOnly = true;

    public BuildCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
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
                    buildBasicRoom(currentRoom, coords, mapMatrix);
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
                    buildBasicRoom(currentRoom, coords, mapMatrix);
                    utils.write(playerId, "Room created.");
                    return;
                } else {
                    utils.write(playerId, "Error!  There is already a room to the South.");
                }
            } else if (desiredBuildDirection.equalsIgnoreCase("e") | desiredBuildDirection.equalsIgnoreCase("east")) {
                if (!currentRoom.getEastId().isPresent() && mapMatrix.getEast(currentRoom.getRoomId()) == 0) {
                    buildBasicRoom(currentRoom, new Coords(roomCoords.row, roomCoords.column + 1), mapMatrix);
                    utils.write(playerId, "Room created.");
                    return;
                } else {
                    utils.write(playerId, "Error!  There is already a room to the East.");
                }
            } else if (desiredBuildDirection.equalsIgnoreCase("w") | desiredBuildDirection.equalsIgnoreCase("west")) {
                if (!currentRoom.getWestId().isPresent() && mapMatrix.getWest(currentRoom.getRoomId()) == 0) {
                    buildBasicRoom(currentRoom, new Coords(roomCoords.row, roomCoords.column - 1), mapMatrix);
                    utils.write(playerId, "Room created.");
                    return;
                } else {
                    utils.write(playerId, "Error!  There is already a room to the West.");
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    private void buildBasicRoom(Room currentRoom, Coords newCords, MapMatrix mapMatrix) {
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

    private Integer findRoomId() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            if (!getGameManager().getRoomManager().doesRoomIdExist(i)) {
                return i;
            }
        }
        return 0;
    }
}
