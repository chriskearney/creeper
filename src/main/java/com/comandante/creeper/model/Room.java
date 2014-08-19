package com.comandante.creeper.model;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.fusesource.jansi.Ansi;

import java.util.Set;

public class Room {

    Ansi roomDescriptionText = new Ansi().fg(Ansi.Color.GREEN);

    public Integer roomId;
    public Optional<Integer> northId;
    public Optional<Integer> westId;
    public Optional<Integer> eastId;
    public Optional<Integer> southId;
    public String roomDescription;
    private Set<String> presentPlayerIds = Sets.<String>newConcurrentHashSet();

    public Room(Integer roomId, Optional<Integer> northId, Optional<Integer> westId, Optional<Integer> eastId, Optional<Integer> southId, String roomDescription) {
        this.roomId = roomId;
        this.northId = northId;
        this.westId = westId;
        this.eastId = eastId;
        this.southId = southId;
        this.roomDescription = roomDescription;
    }

    public java.util.Set<String> getPresentPlayerIds()  {
        return presentPlayerIds;
    }

    public void addPresentPlayer(String playerId) {
        presentPlayerIds.add(playerId);
    }

    public void removePresentPlayer(String playerId) {
        presentPlayerIds.remove(playerId);
    }

    public String getRoomDescription() {
        return new Ansi().fg(Ansi.Color.GREEN).render(roomDescription).toString() + new Ansi().reset().toString();
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
