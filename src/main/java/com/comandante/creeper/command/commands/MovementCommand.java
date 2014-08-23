package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Movement;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
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
    public final static ImmutableList validTriggers =
            new ImmutableList.Builder<String>().addAll(northTriggers).addAll(southTriggers).addAll(eastTriggers).addAll(westTriggers).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public MovementCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        final GameManager gameManager = getGameManager();
        Player player = gameManager.getPlayerManager().getPlayer(getPlayerId());
        Optional<Room> roomOptional = gameManager.getPlayerCurrentRoom(player);
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
                player.getChannel().write("There's no northern exit.\r\n");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getNorthId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the north.");
        }
        if (southTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getSouthId().isPresent()) {
                player.getChannel().write("There's no southern exit.\r\n");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getSouthId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the south.");
        }
        if (eastTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getEastId().isPresent()) {
                player.getChannel().write("There's no eastern exit.\r\n");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getEastId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the east.");
        }
        if (westTriggers.contains(command.toLowerCase())) {
            if (!currentRoom.getWestId().isPresent()) {
                player.getChannel().write("There's no eastern exit.\r\n");
                return;
            }
            Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getWestId().get());
            movement = new Movement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.");
        }
        gameManager.movePlayer(movement);
        gameManager.currentRoomLogic(movement.getPlayer().getPlayerId());
    }
}
