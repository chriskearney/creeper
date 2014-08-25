package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Movement;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.server.ChannelUtils;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

public class MovementCommand extends Command {

    private final static String helpDescription = "Movement command.";
    public final static List<String> northTriggers = Arrays.asList("n", "north".toLowerCase());
    public final static List<String> southTriggers = Arrays.asList("s", "south".toLowerCase());
    public final static List<String> eastTriggers = Arrays.asList("e", "east".toLowerCase());
    public final static List<String> westTriggers = Arrays.asList("w", "west".toLowerCase());
    public final static List<String> upTriggers = Arrays.asList("u", "up".toLowerCase());
    public final static List<String> downTriggers = Arrays.asList("d", "down".toLowerCase());

    public final static ImmutableList validTriggers =
            new ImmutableList.Builder<String>()
                    .addAll(northTriggers)
                    .addAll(southTriggers)
                    .addAll(eastTriggers)
                    .addAll(westTriggers)
                    .addAll(upTriggers)
                    .addAll(downTriggers)
                    .build();
    private final static boolean isCaseSensitiveTriggers = false;

    public MovementCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        final GameManager gameManager = getGameManager();
        Player player = gameManager.getPlayerManager().getPlayer(getPlayerId());
        Optional<Room> roomOptional = gameManager.getRoomManager().getPlayerCurrentRoom(player);
        ChannelUtils channelUtils = gameManager.getChannelUtils();
        if (!roomOptional.isPresent()) {
            throw new RuntimeException("Player is not in a room, movement failed!");
        }
        Room currentRoom = roomOptional.get();
        final String command = getOriginalMessageParts().get(0);
        Movement movement = null;
        if (!validTriggers.contains(command.toLowerCase())) {
            throw new RuntimeException("Malformed movement command.");
        }
        if (northTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getNorthId().isPresent()) {
                channelUtils.write(player.getPlayerId(), "There's no northern exit.");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getNorthId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the north.", "south");
        }
        if (southTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getSouthId().isPresent()) {
                channelUtils.write(player.getPlayerId(), "There's no southern exit.");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getSouthId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the south.", "north");
        }
        if (eastTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getEastId().isPresent()) {
                channelUtils.write(player.getPlayerId(), "There's no eastern exit.");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getEastId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the east.", "west");
        }
        if (westTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getWestId().isPresent()) {
                channelUtils.write(player.getPlayerId(), "There's no western exit.");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getWestId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "east");
        }
        if (upTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getUpId().isPresent()) {
                channelUtils.write(player.getPlayerId(), "There's no up exit.");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getUpId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "down");
        }
        if (downTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getDownId().isPresent()) {
                channelUtils.write(player.getPlayerId(), "There's no down exit.");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getDownId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "up");
        }
        gameManager.movePlayer(movement);
        if (movement != null) {
            player.setReturnDirection(Optional.of(movement.getReturnDirection()));
            gameManager.currentRoomLogic(movement.getPlayer().getPlayerId());
        }
    }
}
