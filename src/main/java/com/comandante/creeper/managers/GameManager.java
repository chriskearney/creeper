package com.comandante.creeper.managers;


import com.comandante.creeper.command.DefaultCommandType;
import com.comandante.creeper.model.Movement;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interners;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.fusesource.jansi.Ansi;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
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
    private final HelpManager helpManager;
    private final NewUserRegistrationManager newUserRegistrationManager;

    public NewUserRegistrationManager getNewUserRegistrationManager() {
        return newUserRegistrationManager;
    }

    public GameManager(RoomManager roomManager, PlayerManager playerManager) {
        this.roomManager = roomManager;
        this.playerManager = playerManager;
        this.newUserRegistrationManager = new NewUserRegistrationManager(playerManager);
        this.helpManager = new HelpManager();
    }

    public HelpManager getHelpManager() {
        return helpManager;
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
            for (String searchPlayerId : room.getPresentPlayerIds()) {
                if (searchPlayerId.equals(player.getPlayerId())) {
                    return Optional.of(room);
                }
            }
        }
        return Optional.absent();
    }

    public Set<Player> getPresentPlayers(Room room) {
        Set<String> presentPlayerIds = room.getPresentPlayerIds();
        Set<Player> players = Sets.newHashSet();
        for (String playerId: presentPlayerIds) {
            players.add(playerManager.getPlayer(playerId));
        }
        return ImmutableSet.copyOf(players);
    }

    public void who(Player player) {
        Set<Player> allPlayers = getAllPlayers();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(new Ansi().fg(Ansi.Color.CYAN).toString());
        stringBuilder.append("----------------------\r\n");
        stringBuilder.append("|--active users------|\r\n");
        stringBuilder.append("----------------------\r\n");
        for (Player allPlayer: allPlayers) {
            stringBuilder.append(allPlayer.getPlayerName()).append("\r\n");
        }
        stringBuilder.append(new Ansi().reset().toString());
        player.getChannel().write(stringBuilder.toString() + "\r\n");
    }

    public void tell(Player sourcePlayer, String rawMessage) {
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(rawMessage.split(" ")));
        if (parts.size() < 3) {
            sourcePlayer.getChannel().write(("tell failed, no message to send.\r\n"));
            return;
        }
        //remove the literal 'tell'
        parts.remove(0);
        String destinationUsername = parts.get(0);
        Player desintationPlayer = getPlayerManager().getPlayerByUsername(destinationUsername);
        if (desintationPlayer == null) {
            sourcePlayer.getChannel().write(("tell failed, unknown user.\r\n"));
            return;
        }
        if (desintationPlayer.getPlayerId().equals(sourcePlayer.getPlayerId())) {
            sourcePlayer.getChannel().write(("tell failed, you're talking to yourself.\r\n"));
            return;
        }
        parts.remove(0);

        String tellMessage = StringUtils.join(parts, " ");
        privateMessage(sourcePlayer, desintationPlayer, tellMessage);

    }

    private void privateMessage(Player sourcePlayer, Player destinationPlayer, String message) {
        StringBuilder stringBuilder = new StringBuilder();
        String sourcePlayerColor = new Ansi().fg(Ansi.Color.WHITE).toString();
        String destinationPlayercolor = new Ansi().fg(Ansi.Color.YELLOW).toString();
        stringBuilder.append("*").append(sourcePlayer.getPlayerName()).append("* ");
        stringBuilder.append(message);
        stringBuilder.append(new Ansi().reset().toString());
        destinationPlayer.getChannel().write(destinationPlayercolor + stringBuilder.toString() + "\r\n");
        sourcePlayer.getChannel().write(sourcePlayerColor + stringBuilder.toString() + "\r\n");
    }

    public Set<Player> getAllPlayers() {
        ImmutableSet.Builder<Player> builder = ImmutableSet.builder();
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Room room = next.getValue();
            Set<Player> presentPlayers = getPresentPlayers(room);
            for (Player player : presentPlayers) {
                builder.add(player);
            }
        }
        return builder.build();
    }

    public void setPlayerAfk(String username) {
        Player playerByUsername = playerManager.getPlayerByUsername(username);
        Optional<Room> playerCurrentRoom = getPlayerCurrentRoom(playerByUsername);
        playerCurrentRoom.get().getPresentPlayerIds().remove(playerByUsername.getPlayerId());
        playerCurrentRoom.get().addAfkPlayer(playerByUsername.getPlayerId());
    }

    public void movePlayer(Movement movement) {
        synchronized (Interners.newStrongInterner()) {
            Room sourceRoom = roomManager.getRoom(movement.getSourceRoomId());
            Room destinationRoom = roomManager.getRoom(movement.getDestinationRoomId());
            sourceRoom.removePresentPlayer(movement.getPlayer().getPlayerId());
            for (Player next : getPresentPlayers(sourceRoom)) {
                StringBuilder sb = new StringBuilder();
                sb.append(movement.getPlayer().getPlayerName());
                if (movement.getOriginalMovementCommand().equals(DefaultCommandType.MOVE_NORTH)) {
                    sb.append(" exited to the north.");
                } else if (movement.getOriginalMovementCommand().equals(DefaultCommandType.MOVE_EAST)) {
                    sb.append(" exited to the east.");
                } else if (movement.getOriginalMovementCommand().equals(DefaultCommandType.MOVE_SOUTH)) {
                    sb.append(" exited to the south.");
                } else if (movement.getOriginalMovementCommand().equals(DefaultCommandType.MOVE_WEST)) {
                    sb.append(" exited to the west.");
                } else {
                    sb.append(" exited.");
                }
                sb.append("\r\n");
            next.getChannel().write(sb.toString());
            }
            for (Player next : getPresentPlayers(destinationRoom)) {
                next.getChannel().write(movement.getPlayer().getPlayerName() + " arrived.\r\n");
            }
            destinationRoom.addPresentPlayer(movement.getPlayer().getPlayerId());
        }
    }

    public void placePlayerInLobby(Player player) {
        Room room = roomManager.getRoom(LOBBY_ID);
        room.addPresentPlayer(player.getPlayerId());
        for (Player next : getPresentPlayers(room)) {
            if (next.getPlayerId().equals(player.getPlayerId())) {
                continue;
            }
            next.getChannel().write(player.getPlayerName() + " arrived.\r\n");
        }
    }

    public void say(Player sourcePlayer, String message) {
        Optional<Room> playerCurrentRoomOpt = getPlayerCurrentRoom(sourcePlayer);
        if (!playerCurrentRoomOpt.isPresent()) {
            throw new RuntimeException("playerCurrentRoom is missing!");
        }

        Room playerCurrentRoom = playerCurrentRoomOpt.get();
        Set<Player> presentPlayers = getPresentPlayers(playerCurrentRoom);

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
        stringBuilder.append("exits: ");
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
        final Player player = playerManager.getPlayerByUsername(creeperSession.getUsername().get());
        final Room playerCurrentRoom = getPlayerCurrentRoom(player).get();
        e.getChannel().write(playerCurrentRoom.getRoomDescription() + "\r\n");
        for (String searchPlayerId : playerCurrentRoom.getPresentPlayerIds()) {
            if (searchPlayerId.equals(player.getPlayerId())) {
                continue;
            }
            Player searchPlayer = playerManager.getPlayer(searchPlayerId);
            e.getChannel().write(searchPlayer.getPlayerName() + " is here.\r\n");
        }
        printExits(playerCurrentRoom, e.getChannel());
    }

}
