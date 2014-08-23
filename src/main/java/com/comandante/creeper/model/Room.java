package com.comandante.creeper.model;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.fusesource.jansi.Ansi;

import java.util.Set;

public class Room {

    public Integer roomId;
    public Optional<Integer> northId;
    public Optional<Integer> westId;
    public Optional<Integer> eastId;
    public Optional<Integer> southId;
    public String roomDescription;
    private Set<String> presentPlayerIds = Sets.<String>newConcurrentHashSet();
    private Set<String> afkPlayerIds = Sets.<String>newConcurrentHashSet();
    private Set<String> npcIds = Sets.newConcurrentHashSet();

    public Room(Integer roomId, Optional<Integer> northId, Optional<Integer> westId, Optional<Integer> eastId, Optional<Integer> southId, String roomDescription) {
        this.roomId = roomId;
        this.northId = northId;
        this.westId = westId;
        this.eastId = eastId;
        this.southId = southId;
        this.roomDescription = roomDescription;
    }

    public void addPresentNpc(String npcId) {
        npcIds.add(npcId);
    }

    public void removePresentNpc(String npcId) {
        npcIds.remove(npcId);
    }

    public Set<String> getNpcIds() {
        return npcIds;
    }

    public java.util.Set<String> getPresentPlayerIds()  {
        return presentPlayerIds;
    }

    public Set<String> getAfkPlayerIds() {
        return afkPlayerIds;
    }

    public void addPresentPlayer(String playerId) {
        presentPlayerIds.add(playerId);
    }

    public void removePresentPlayer(String playerId) {
        presentPlayerIds.remove(playerId);
    }

    public void addAfkPlayer(String playerId) {
        afkPlayerIds.add(playerId);
    }

    public void removeAfkPlayer(String playerId) {
        afkPlayerIds.remove(playerId);
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
