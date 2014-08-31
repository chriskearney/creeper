package com.comandante.creeper.command.commands;

import com.comandante.creeper.fight.FightManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.room.Room;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.CreeperSession;
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

    public final static ImmutableList validTriggers =
            new ImmutableList.Builder<String>()
                    .addAll(northTriggers)
                    .addAll(southTriggers)
                    .addAll(eastTriggers)
                    .addAll(westTriggers)
                    .addAll(upTriggers)
                    .addAll(downTriggers)
                    .build();

    public MovementCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            final GameManager gameManager = getGameManager();
            CreeperSession session = getCreeperSession(e.getChannel());
            Player player = gameManager.getPlayerManager().getPlayer(getPlayerId(session));
            Optional<Room> roomOptional = gameManager.getRoomManager().getPlayerCurrentRoom(player);
            ChannelUtils channelUtils = gameManager.getChannelUtils();
            if (!roomOptional.isPresent()) {
                throw new RuntimeException("Player is not in a room, movement failed!");
            }
            if (FightManager.isActiveFight(session)) {
                channelUtils.write(getPlayerId(session), "You can't not move while in a fight!");
                return;
            }
            Room currentRoom = roomOptional.get();
            final String command = getRootCommand(e);
            PlayerMovement playerMovement = null;
            if (!validTriggers.contains(command.toLowerCase())) {
                throw new RuntimeException("Malformed movement command.");
            }
            if (northTriggers.contains(command.toLowerCase())) {
                if (!currentRoom.getNorthId().isPresent()) {
                    channelUtils.write(getPlayerId(session), "There's no northern exit.");
                    return;
                }
                Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getNorthId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the north.", "south");
            }
            if (southTriggers.contains(command.toLowerCase())) {
                if (!currentRoom.getSouthId().isPresent()) {
                    channelUtils.write(getPlayerId(session), "There's no southern exit.");
                    return;
                }
                Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getSouthId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the south.", "north");
            }
            if (eastTriggers.contains(command.toLowerCase())) {
                if (!currentRoom.getEastId().isPresent()) {
                    channelUtils.write(getPlayerId(session), "There's no eastern exit.");
                    return;
                }
                Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getEastId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the east.", "west");
            }
            if (westTriggers.contains(command.toLowerCase())) {
                if (!currentRoom.getWestId().isPresent()) {
                    channelUtils.write(getPlayerId(session), "There's no western exit.");
                    return;
                }
                Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getWestId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "east");
            }
            if (upTriggers.contains(command.toLowerCase())) {
                if (!currentRoom.getUpId().isPresent()) {
                    channelUtils.write(getPlayerId(session), "There's no up exit.");
                    return;
                }
                Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getUpId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "down");
            }
            if (downTriggers.contains(command.toLowerCase())) {
                if (!currentRoom.getDownId().isPresent()) {
                    channelUtils.write(getPlayerId(session), "There's no down exit.");
                    return;
                }
                Room destinationRoom = gameManager.getRoomManager().getRoom(currentRoom.getDownId().get());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), this, "exited to the west.", "up");
            }
            gameManager.movePlayer(playerMovement);
            if (playerMovement != null) {
                player.setReturnDirection(Optional.of(playerMovement.getReturnDirection()));
                gameManager.currentRoomLogic(playerMovement.getPlayer().getPlayerId());
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
