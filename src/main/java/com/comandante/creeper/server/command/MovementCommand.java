package com.comandante.creeper.server.command;

import com.comandante.creeper.fight.FightManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.world.Room;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class MovementCommand extends Command {

    final static String description = "Make a move.";

    public final static List<String> northTriggers = Arrays.asList("n", "north".toLowerCase());
    public final static List<String> southTriggers = Arrays.asList("s", "south".toLowerCase());
    public final static List<String> eastTriggers = Arrays.asList("e", "east".toLowerCase());
    public final static List<String> westTriggers = Arrays.asList("w", "west".toLowerCase());
    public final static List<String> upTriggers = Arrays.asList("u", "up".toLowerCase());
    public final static List<String> downTriggers = Arrays.asList("d", "down".toLowerCase());

    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().addAll(northTriggers).addAll(southTriggers).addAll(eastTriggers).addAll(westTriggers).addAll(upTriggers).addAll(downTriggers).build();

    public MovementCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (FightManager.isActiveFight(creeperSession)) {
                write("You can't not move while in a fight!");
                return;
            }
            final String command = getRootCommand(e);
            PlayerMovement playerMovement = null;
            if (!validTriggers.contains(command.toLowerCase())) {
                throw new RuntimeException("Malformed movement command.");
            } else if (northTriggers.contains(command.toLowerCase()) && currentRoom.getNorthId().isPresent()) {
                Room destinationRoom = roomManager.getRoom(currentRoom.getNorthId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the north.", "south");
            } else if (southTriggers.contains(command.toLowerCase()) && currentRoom.getSouthId().isPresent()) {
                Room destinationRoom = roomManager.getRoom(currentRoom.getSouthId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the south.", "north");
            } else if (eastTriggers.contains(command.toLowerCase()) && currentRoom.getEastId().isPresent()) {
                Room destinationRoom = roomManager.getRoom(currentRoom.getEastId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the east.", "west");
            } else if (westTriggers.contains(command.toLowerCase()) && currentRoom.getWestId().isPresent()) {
                Room destinationRoom = roomManager.getRoom(currentRoom.getWestId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "east");
            } else if (upTriggers.contains(command.toLowerCase()) && currentRoom.getUpId().isPresent()) {
                Room destinationRoom = roomManager.getRoom(currentRoom.getUpId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "down");
            } else if (downTriggers.contains(command.toLowerCase()) && currentRoom.getDownId().isPresent()) {
                Room destinationRoom = roomManager.getRoom(currentRoom.getDownId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "up");
            } else {
                write("There's no exit in that direction. (" + command + ")");
                return;
            }
            gameManager.movePlayer(playerMovement);
            if (playerMovement != null) {
                player.setReturnDirection(Optional.of(playerMovement.getReturnDirection()));
                currentRoomLogic();
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
