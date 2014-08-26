package com.comandante.creeper.model;

import com.comandante.creeper.Items.ItemSpawner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class Room extends CreeperEntity {

    private final Integer roomId;
    private final String roomTitle;
    private final Optional<Integer> northId;
    private final Optional<Integer> westId;
    private final Optional<Integer> eastId;
    private final Optional<Integer> southId;
    private final Optional<Integer> downId;
    private final Optional<Integer> upId;
    private final String roomDescription;
    private final Set<String> presentPlayerIds = Sets.<String>newConcurrentHashSet();
    private final Set<String> afkPlayerIds = Sets.<String>newConcurrentHashSet();
    private final Set<String> npcIds = Sets.newConcurrentHashSet();
    private final Set<String> itemIds = Sets.newConcurrentHashSet();
    private List<ItemSpawner> itemSpawners = Lists.newArrayList();

    public Room(Integer roomId,
                String roomTitle,
                Optional<Integer> northId,
                Optional<Integer> southId,
                Optional<Integer> eastId,
                Optional<Integer> westId,
                Optional<Integer> upId,
                Optional<Integer> downId,
                String roomDescription) {
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.northId = northId;
        this.westId = westId;
        this.eastId = eastId;
        this.southId = southId;
        this.upId = upId;
        this.downId = downId;
        this.roomDescription = roomDescription;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void addPresentItem(String itemId) {
        itemIds.add(itemId);
    }

    public Set<String> getItemIds() {
        return itemIds;
    }

    public void remotePresentItem(String itemId) {
        itemIds.remove(itemId);

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

    public Optional<Integer> getUpId() {
        return upId;
    }

    public Optional<Integer> getDownId() {
        return downId;
    }

    public void addItemSpawner(ItemSpawner itemSpawner) {
        itemSpawners.add(itemSpawner);
    }

    @Override
    public void run() {
        Iterator<ItemSpawner> iterator = itemSpawners.iterator();
        while (iterator.hasNext()) {
            ItemSpawner next = iterator.next();
            next.run();
        }

    }
}
