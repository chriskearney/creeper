package com.comandante;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

public class Room {

    public Integer roomId;
    public Optional<Integer> northId;
    public Optional<Integer> westId;
    public Optional<Integer> eastId;
    public Optional<Integer> southId;
    public String roomDescription;
    private Set<Player> presentPlayers = Sets.<Player>newConcurrentHashSet();

    public Room(Integer roomId, Optional<Integer> northId, Optional<Integer> westId, Optional<Integer> eastId, Optional<Integer> southId, String roomDescription) {
        this.roomId = roomId;
        this.northId = northId;
        this.westId = westId;
        this.eastId = eastId;
        this.southId = southId;
        this.roomDescription = roomDescription;
    }

    public java.util.Set<Player> getPresentPlayers()  {
        return ImmutableSet.<Player>builder().addAll(presentPlayers.iterator()).build();
    }

    public void addPresentPlayer(Player player) {
        presentPlayers.add(player);
    }

    public void removePresentPlayer(Player player) {
        presentPlayers.remove(player);
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public Optional<Integer> getNorthId() {
        return northId;
    }

    public Optional<Integer> getWestId() {
        return westId;
    }

    public Optional<Integer> getEastId() {
        return eastId;
    }

    public Optional<Integer> getSouthId() {
        return southId;
    }
}
