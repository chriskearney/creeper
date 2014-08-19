package com.comandante.creeper.managers;


import com.comandante.creeper.model.Movement;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Optional;
import com.google.common.collect.Interners;
import org.fusesource.jansi.Ansi;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GameManager {

    public static String LOGO =
            " ██████╗██████╗ ███████╗███████╗██████╗ ███████╗██████╗ \r\n" +
            "██╔════╝██╔══██╗██╔════╝██╔════╝██╔══██╗██╔════╝██╔══██╗\r\n" +
            "██║     ██████╔╝█████╗  █████╗  ██████╔╝█████╗  ██████╔╝\r\n" +
            "██║     ██╔══██╗██╔══╝  ██╔══╝  ██╔═══╝ ██╔══╝  ██╔══██╗\r\n" +
            "╚██████╗██║  ██║███████╗███████╗██║     ███████╗██║  ██║\r\n" +
            " ╚═════╝╚═╝  ╚═╝╚══════╝╚══════╝╚═╝     ╚══════╝╚═╝  ╚═╝";

    public static String VERSION = "1.0-SNAPSHOT";

    private final RoomManager roomManager;
    private final PlayerManager playerManager;

    public GameManager(RoomManager roomManager, PlayerManager playerManager) {
        this.roomManager = roomManager;
        this.playerManager = playerManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    private static final Integer LOBBY_ID = 1;

    public Optional<Room> getPlayerCurrentRoom(Player player) {
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Room room = next.getValue();
            for (Player searchPlayer : room.getPresentPlayers()) {
                if (searchPlayer.getPlayerId().equals(player.getPlayerId())) {
                    return Optional.of(room);
                }
            }
        }
        return Optional.absent();
    }

    public void movePlayer(Movement movement) {
        synchronized (Interners.newStrongInterner()) {
            Room sourceRoom = roomManager.getRoom(movement.getSourceRoomId());
            Room destinationRoom = roomManager.getRoom(movement.getDestinationRoomId());
            sourceRoom.removePresentPlayer(movement.getPlayer());
            for (Player next : sourceRoom.getPresentPlayers()) {
                next.getChannel().write(movement.getPlayer().getPlayerName() + " used exit: " + movement.getOriginalMovementCommand() + ".\r\n");
            }
            for (Player next : destinationRoom.getPresentPlayers()) {
                next.getChannel().write(movement.getPlayer().getPlayerName() + " arrived.\r\n");
            }
            destinationRoom.addPresentPlayer(movement.getPlayer());
        }
    }

    public void placePlayerInLobby(Player player) {
        roomManager.getRoom(LOBBY_ID).addPresentPlayer(player);
    }

    public void say(Player sourcePlayer, String message) {
        Optional<Room> playerCurrentRoomOpt = getPlayerCurrentRoom(sourcePlayer);
        if (!playerCurrentRoomOpt.isPresent()) {
            throw new RuntimeException("playerCurrentRoom is missing!");
        }

        Room playerCurrentRoom = playerCurrentRoomOpt.get();
        Set<Player> presentPlayers = playerCurrentRoom.getPresentPlayers();

        for (Player presentPlayer : presentPlayers) {
            StringBuilder stringBuilder = new StringBuilder();
            if (presentPlayer.getPlayerId().equals(sourcePlayer.getPlayerId())) {
                stringBuilder.append(new Ansi().fg(Ansi.Color.WHITE).toString());
            } else {
                stringBuilder.append(new Ansi().fg(Ansi.Color.RED).toString());
            }
            stringBuilder.append("<").append(sourcePlayer.getPlayerName()).append("> ").append(message).append("\r\n");
            stringBuilder.append(new Ansi().reset().toString());
            presentPlayer.getChannel().write(stringBuilder.toString());
        }
    }

    public void gossip(Player sourcePlayer, String message) {
        Iterator<Map.Entry<String, Player>> players = playerManager.getPlayers();
        while (players.hasNext()) {
            StringBuilder stringBuilder = new StringBuilder();
            Player player = players.next().getValue();
            if (player.getPlayerId().equals(sourcePlayer.getPlayerId())) {
                stringBuilder.append(new Ansi().fg(Ansi.Color.WHITE).toString());
            } else {
                stringBuilder.append(new Ansi().fg(Ansi.Color.MAGENTA).toString());
            }
            stringBuilder.append("[").append(sourcePlayer.getPlayerName()).append("] ").append(message).append("\r\n");
            stringBuilder.append(new Ansi().reset().toString());
            player.getChannel().write(stringBuilder.toString());
        }
    }

    private void printExits(Room room, Channel channel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("move: ");
        stringBuilder.append(new Ansi().fg(Ansi.Color.BLUE).toString());
        if (room.getNorthId().isPresent()) {
            stringBuilder.append("north ");
        }
        if (room.getSouthId().isPresent()) {
            stringBuilder.append("south ");
        }
        if (room.getEastId().isPresent()) {
            stringBuilder.append("east ");
        }
        if (room.getWestId().isPresent()) {
            stringBuilder.append("west ");
        }
        stringBuilder.append(new Ansi().reset().toString());
        channel.write(stringBuilder.toString() + "\r\n");
    }

    public void currentRoomLogic(CreeperSession creeperSession, MessageEvent e) {
        final Player player = playerManager.getPlayer(creeperSession.getUsername().get());
        final Room playerCurrentRoom = getPlayerCurrentRoom(player).get();
        e.getChannel().write(playerCurrentRoom.getRoomDescription() + "\r\n");
        for (Player next : playerCurrentRoom.getPresentPlayers()) {
            if (next.getPlayerId().equals(new Player(creeperSession.getUsername().get()).getPlayerId())) {
                continue;
            }
            e.getChannel().write(next.getPlayerName() + " is here.\r\n");
        }
        printExits(playerCurrentRoom, e.getChannel());
    }

}
