package com.comandante.creeper.world.model;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.service.TimeOfDay;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.items.Forage;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.spawner.ItemSpawner;
import com.comandante.creeper.world.RoomManager;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
    private List<RemoteExit> enterExits = Lists.newArrayList();
    private String roomDescription;
    private final Set<String> presentPlayerIds = Sets.<String>newConcurrentHashSet();
    private final Set<String> npcIds = Sets.newConcurrentHashSet();
    private final Set<String> itemIds = Sets.newConcurrentHashSet();
    private List<ItemSpawner> itemSpawners = Lists.newArrayList();
    private Set<Area> areas = Sets.newConcurrentHashSet();
    private Optional<String> mapData = Optional.empty();
    private final Set<String> roomTags;
    private final Set<Merchant> merchants = Sets.newConcurrentHashSet();
    private Map<String, Forage> forages = Maps.newHashMap();
    private Set<String> requiredInternalItemNames = Sets.newHashSet();
    private final Map<String, String> notables;
    private final GameManager gameManager;
    private Optional<Integer> minimumLevel;

    public Room(Integer roomId,
                String roomTitle,
                Integer floorId,
                Optional<Integer> northId,
                Optional<Integer> southId,
                Optional<Integer> eastId,
                Optional<Integer> westId,
                Optional<Integer> upId,
                Optional<Integer> downId,
                List<RemoteExit> enterExits,
                String roomDescription, Set<String> roomTags,
                Set<Area> areas,
                Map<String, String> notables,
                Set<String> requiredInternalItemNames,
                Optional<Integer> minimumLevel,
                GameManager gameManager) {
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
        this.enterExits = enterExits;
        this.notables = notables;
        this.requiredInternalItemNames = requiredInternalItemNames;
        this.minimumLevel = minimumLevel;
        this.gameManager = gameManager;
    }

    public List<ItemSpawner> getItemSpawners() {
        return itemSpawners;
    }

    public void setItemSpawners(List<ItemSpawner> itemSpawners) {
        this.itemSpawners = itemSpawners;
    }

    public Set<Merchant> getMerchants() {
        return merchants;
    }

    public void addMerchant(Merchant merchant) {
        merchants.add(merchant);
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

    public Optional<Integer> getMinimumLevel() {
        return minimumLevel;
    }

    public Set<String> getRequiredInternalItemNames() {
        return requiredInternalItemNames;
    }

    public void setMinimumLevel(Integer level) {
        this.minimumLevel = Optional.ofNullable(level);
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

    public Set<String> getPresentPlayerIds() {
        return presentPlayerIds.stream().filter(playerId -> gameManager.getPlayerManager().getPlayer(playerId) != null).collect(Collectors.toSet());
    }

    public Set<Player> getPresentPlayers() {
        Set<Player> players = Sets.newHashSet();
        for (String playerId : presentPlayerIds) {
            Player player = gameManager.getPlayerManager().getPlayer(playerId);
            if (player != null) {
                players.add(player);
            }
        }
        return ImmutableSet.copyOf(players);
    }

    public void addPresentPlayer(String playerId) {
        presentPlayerIds.add(playerId);

    }

    public void removePresentPlayer(String playerId) {
        presentPlayerIds.remove(playerId);
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

    public List<RemoteExit> getEnterExits() {
        return enterExits;
    }

    public void addEnterExit(RemoteExit remoteExit) {
        enterExits.add(remoteExit);
    }

    public void setEnterExits(List<RemoteExit> enterExits) {
        this.enterExits = enterExits;
    }

    public void addItemSpawner(ItemSpawner itemSpawner) {
        itemSpawners.add(itemSpawner);
    }

    public Map<String, Forage> getForages() {
        return forages;
    }

    public void addForage(Forage forage) {
        this.forages.put(forage.getInternalItemName(), forage);
    }

    public Map<String, String> getNotables() {
        return notables;
    }

    public void addNotable(String notableName, String description) {
        notables.put(notableName, description);
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public List<Npc> getPresentNpcs() {
        return npcIds.stream().map(s -> gameManager.getEntityManager().getNpcEntity(s)).collect(Collectors.toList());
    }

    public List<Item> getPresentItems() {
        return itemIds.stream()
                .map(s -> gameManager.getEntityManager().getItemEntity(s))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private boolean isValid(Direction direction) {
        if (direction.equals(Direction.NORTH)) {
            return getNorthId().isPresent();
        } else if (direction.equals(Direction.SOUTH)) {
            return getSouthId().isPresent();
        } else if (direction.equals(Direction.EAST)) {
            return getEastId().isPresent();
        } else if (direction.equals(Direction.WEST)) {
            return getWestId().isPresent();
        } else if (direction.equals(Direction.DOWN)) {
            return getDownId().isPresent();
        } else if (direction.equals(Direction.UP)) {
            return getUpId().isPresent();
        } else if (direction.equals(Direction.ENTER)) {
            return direction.getRemoteExit().isPresent();
        }

        return false;
    }

    public Optional<PlayerMovement> derivePlayerMovement(Player player, Direction direction) {
        PlayerMovement playerMovement = null;
        RoomManager roomManager = gameManager.getRoomManager();
        if (!isValid(direction)) {
            return Optional.empty();
        }
        if (direction.equals(Direction.NORTH)) {
            Room destinationRoom = roomManager.getRoom(getNorthId().get());
            playerMovement = new PlayerMovement(player, getRoomId(), destinationRoom.getRoomId(), direction);
        } else if (direction.equals(Direction.SOUTH)) {
            Room destinationRoom = roomManager.getRoom(getSouthId().get());
            playerMovement = new PlayerMovement(player, getRoomId(), destinationRoom.getRoomId(), direction);
        } else if (direction.equals(Direction.EAST)) {
            Room destinationRoom = roomManager.getRoom(getEastId().get());
            playerMovement = new PlayerMovement(player, getRoomId(), destinationRoom.getRoomId(), direction);
        } else if (direction.equals(Direction.WEST)) {
            Room destinationRoom = roomManager.getRoom(getWestId().get());
            playerMovement = new PlayerMovement(player, getRoomId(), destinationRoom.getRoomId(), direction);
        } else if (direction.equals(Direction.UP)) {
            Room destinationRoom = roomManager.getRoom(getUpId().get());
            playerMovement = new PlayerMovement(player, getRoomId(), destinationRoom.getRoomId(), direction);
        } else if (direction.equals(Direction.DOWN)) {
            Room destinationRoom = roomManager.getRoom(getDownId().get());
            playerMovement = new PlayerMovement(player, getRoomId(), destinationRoom.getRoomId(), direction);
        } else if (direction.equals(Direction.ENTER)) {
            if (direction.getRemoteExit().isPresent()) {
                Room destinationRoom = roomManager.getRoom(direction.getRemoteExit().get().getRoomId());
                playerMovement = new PlayerMovement(player, getRoomId(), destinationRoom.getRoomId(), direction);
            }
        }
        return Optional.ofNullable(playerMovement);
    }

    public enum Direction {
        NORTH("north", "exited to the north.", Lists.newArrayList("n", "north".toLowerCase()), "south"),
        SOUTH("south", "exited to the south.", Lists.newArrayList("s", "south".toLowerCase()), "north"),
        EAST("east", "exited to the east.", Lists.newArrayList("e", "east".toLowerCase()), "west"),
        WEST("west", "exited to the west.", Lists.newArrayList("w", "west".toLowerCase()), "east"),
        UP("north", "exited up.", Lists.newArrayList("u", "up".toLowerCase()), "down"),
        DOWN("north", "exited down.", Lists.newArrayList("d", "down".toLowerCase()), "up"),
        ENTER("north", "entered ", Lists.newArrayList("enter", "enter".toLowerCase()), "");

        private final String direction;
        private final String exitMessage;
        private final List<String> validTriggers;
        private final String returnDirection;
        public final static List<String> allTriggers = Arrays.stream(values())
                .map(Direction::getValidTriggers)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        private Optional<RemoteExit> remoteExit = Optional.empty();

        Direction(String direction, String exitMessage, List<String> validTriggers, String returnDirection) {
            this.direction = direction;
            this.exitMessage = exitMessage;
            this.validTriggers = validTriggers;
            this.returnDirection = returnDirection;
        }

        public String getDirection() {
            return direction;
        }

        public void setRemoteExit(Optional<RemoteExit> remoteExit) {
            this.remoteExit = remoteExit;
        }

        public Optional<RemoteExit> getRemoteExit() {
            return remoteExit;
        }

        public List<String> getValidTriggers() {
            return validTriggers;
        }

        public String getReturnDirection() {
            return returnDirection;
        }

        public String getExitMessage() {
            if (this.equals(ENTER) && remoteExit.isPresent()) {
                return this.exitMessage + remoteExit.get().getExitDetail();
            }
            return exitMessage;
        }

        public static Optional<Direction> from(String direction) {
            for (Direction d: values()) {
                if (d.getValidTriggers().contains(direction.toLowerCase())) {
                    return Optional.of(d);
                }
            }
            return Optional.empty();
        }
    }



    @Override
    public void run() {
        for (String itemId : itemIds) {
            Optional<Item> itemOptional = gameManager.getEntityManager().getItemEntity(itemId);
            if (!itemOptional.isPresent()) {
                removePresentItem(itemId);
                continue;
            }
            Item itemEntity = itemOptional.get();
            if (itemEntity.isHasBeenWithPlayer()) {
                continue;
            }
            Set<TimeOfDay> itemValidTimeOfDays = itemEntity.getValidTimeOfDays();
            TimeOfDay timeOfDay = gameManager.getTimeTracker().getTimeOfDay();
            if (itemValidTimeOfDays.size() > 0 && !itemValidTimeOfDays.contains(timeOfDay)) {
                gameManager.getEntityManager().removeItem(itemId);
                removePresentItem(itemId);
                gameManager.writeToRoom(roomId, itemEntity.getItemName() + " turns to dust.\r\n");
            }
        }
    }
}
