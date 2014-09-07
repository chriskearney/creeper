package com.comandante.creeper.world;

import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.spawner.ItemSpawner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class Room extends CreeperEntity {

    private final Integer roomId;

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    private String roomTitle;
    private final Integer floorId;
    private Optional<Integer> northId;
    private Optional<Integer> westId;
    private Optional<Integer> eastId;
    private Optional<Integer> southId;
    private Optional<Integer> downId;
    private Optional<Integer> upId;
    private String roomDescription;
    private final Set<String> presentPlayerIds = Sets.<String>newConcurrentHashSet();
    private final Set<String> afkPlayerIds = Sets.<String>newConcurrentHashSet();
    private final Set<String> npcIds = Sets.newConcurrentHashSet();
    private final Set<String> itemIds = Sets.newConcurrentHashSet();
    private List<ItemSpawner> itemSpawners = Lists.newArrayList();
    private Set<Area> areas = Sets.newHashSet(Area.DEFAULT);
    private Optional<String> mapData = Optional.absent();
    private final Set<String> roomTags;

    public Room(Integer roomId,
                String roomTitle,
                Integer floorId,
                Optional<Integer> northId,
                Optional<Integer> southId,
                Optional<Integer> eastId,
                Optional<Integer> westId,
                Optional<Integer> upId,
                Optional<Integer> downId,
                String roomDescription, Set<String> roomTags,
                Set<Area> areas) {
        this.roomId = roomId;
        this.roomTitle = roomTitle;
        this.floorId = floorId;
        this.northId = northId;
        this.westId = westId;
        this.eastId = eastId;
        this.southId = southId;
        this.upId = upId;
        this.downId = downId;
        this.roomDescription = roomDescription;
        this.roomTags = roomTags;
        this.areas = areas;
    }

    public Set<String> getRoomTags() {
        return roomTags;
    }

    public void setNorthId(Optional<Integer> northId) {
        this.northId = northId;
    }

    public void setWestId(Optional<Integer> westId) {
        this.westId = westId;
    }

    public void setEastId(Optional<Integer> eastId) {
        this.eastId = eastId;
    }

    public void setSouthId(Optional<Integer> southId) {
        this.southId = southId;
    }

    public void setDownId(Optional<Integer> downId) {
        this.downId = downId;
    }

    public void setUpId(Optional<Integer> upId) {
        this.upId = upId;
    }

    public void addTag(String tag) {
        roomTags.add(tag);
    }
    public Integer getFloorId() {
        return floorId;
    }

    public Optional<String> getMapData() {
        return mapData;
    }

    public void setMapData(Optional<String> mapData) {
        this.mapData = mapData;
    }

    public Set<Area> getAreas() {
        return areas;
    }

    public void setAreas(Set<Area> areas) {
        this.areas = areas;
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

    public void removePresentItem(String itemId) {
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

    public java.util.Set<String> getPresentPlayerIds() {
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
