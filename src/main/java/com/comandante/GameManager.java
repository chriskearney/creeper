package com.comandante;

import com.google.common.base.Optional;
import com.google.common.collect.Interners;
import org.fusesource.jansi.Ansi;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GameManager {

    private ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();
    private ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>();

    public void addRoom(Room room) {
        rooms.put(room.roomId, room);
    }

    public Player addPlayer(Player player) {
        return players.putIfAbsent(player.getPlayerId(), player);
    }

    public void removePlayer(Player player) {
        if (getPlayer(player).getChannel() != null) {
            getPlayer(player).getChannel().disconnect();
        }
        players.remove(player);
        Iterator<Integer> iterator = rooms.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            rooms.get(next).removePresentPlayer(player);
        }
    }

    public boolean doesPlayerExist(Player player) {
        for (Map.Entry<String, Player> stringPlayerEntry : players.entrySet()) {
            Map.Entry pairs = (Map.Entry) stringPlayerEntry;
            Player playerRetrieved = (Player) pairs.getValue();
            if (playerRetrieved.getPlayerId().equals(player.getPlayerId())) {
                return true;
            }
        }
        return false;
    }

    public void gossip(Player sourcePlayer, String message) {
        for (Map.Entry<String, Player> next : players.entrySet()) {
            StringBuilder stringBuilder = new StringBuilder();
            Player player = next.getValue();
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

    public void say(Player sourcePlayer, String message) {
        Optional<Room> playerCurrentRoom = getPlayerCurrentRoom(sourcePlayer);
        Iterator<Player> iterator = playerCurrentRoom.get().getPresentPlayers().iterator();
        while (iterator.hasNext()) {
            StringBuilder stringBuilder = new StringBuilder();
            Player player = iterator.next();
            if (player.getPlayerId().equals(sourcePlayer.getPlayerId())) {
                stringBuilder.append(new Ansi().fg(Ansi.Color.WHITE).toString());
            } else {
                stringBuilder.append(new Ansi().fg(Ansi.Color.RED).toString());
            }
            stringBuilder.append("<").append(sourcePlayer.getPlayerName()).append("> ").append(message).append("\r\n");
            stringBuilder.append(new Ansi().reset().toString());
            player.getChannel().write(stringBuilder.toString());
        }
    }

    public void movePlayer(Movement movement) {
        synchronized (Interners.newStrongInterner()) {
            Room sourceRoom = rooms.get(movement.getSourceRoomId());
            sourceRoom.removePresentPlayer(movement.getPlayer());
            Iterator<Player> iterator = sourceRoom.getPresentPlayers().iterator();
            while (iterator.hasNext()) {
                Player next = iterator.next();
                next.getChannel().write(movement.getPlayer().getPlayerName() + " used exit: " + movement.getOriginalMovementCommand() + ".\r\n");
            }
            Room destinationRoom = rooms.get(movement.getDestinationRoomId());
            Iterator<Player> iterator1 = destinationRoom.getPresentPlayers().iterator();
            while (iterator1.hasNext()) {
                Player next = iterator1.next();
                next.getChannel().write(movement.getPlayer().getPlayerName() + " arrived.\r\n");
            }
            destinationRoom.addPresentPlayer(movement.getPlayer());
        }
    }

    public void addPlayerToLobby(Player player) {
        rooms.get(1).addPresentPlayer(player);
    }

    public Player getPlayer(Player player) {
        return players.get(player.getPlayerId());
    }

    public Optional<Room> getPlayerCurrentRoom(Player player) {
        Iterator<Map.Entry<Integer, Room>> iterator = rooms.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Room> next = iterator.next();
            Set<Player> presentPlayers = next.getValue().getPresentPlayers();
            Iterator<Player> iterator1 = presentPlayers.iterator();
            while (iterator1.hasNext()) {
                Player next1 = iterator1.next();
                if (next1.getPlayerId().equals(player.getPlayerId())) {
                    return Optional.of(next.getValue());
                }
            }
        }
        return Optional.absent();
    }

}
