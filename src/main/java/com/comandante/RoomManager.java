package com.comandante;

import com.google.common.base.Optional;
import com.google.common.collect.Interners;
import com.google.common.util.concurrent.AbstractExecutionThreadService;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class RoomManager extends AbstractExecutionThreadService {

    ArrayBlockingQueue<Movement> movements = new ArrayBlockingQueue<Movement>(10000);

    private ConcurrentHashMap<Integer, Room> rooms = new ConcurrentHashMap<Integer, Room>();
    private ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<String, Player>();

    private AtomicBoolean isRunning = new AtomicBoolean(false);

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

    public void updatePlayerMovement(Movement movement) throws InterruptedException {
        movements.put(movement);
    }

    public void _processMovment(Movement movement) {
        synchronized (Interners.newStrongInterner()) {
            rooms.get(movement.getSourceRoomId()).removePresentPlayer(movement.getPlayer());
            rooms.get(movement.getDestinationRoomId()).addPresentPlayer(movement.getPlayer());
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

    @Override
    protected void startUp() throws Exception {
        isRunning.set(true);
    }

    @Override
    protected void shutDown() throws Exception {
        isRunning.set(false);

    }

    @Override
    protected void run() throws Exception {
        while (isRunning.get()) {
            Movement take = movements.take();
            _processMovment(take);
        }
    }
}
