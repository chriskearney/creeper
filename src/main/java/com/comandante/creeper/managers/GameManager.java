package com.comandante.creeper.managers;


import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemDecayManager;
import com.comandante.creeper.model.Movement;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interners;
import org.apache.commons.lang3.text.WordUtils;
import org.jboss.netty.channel.MessageEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static com.comandante.creeper.model.Color.*;

public class GameManager {

    public static String LOGO = "\r\n" +
            "               .------..                            _------__--___.__.\r\n" +
            "            /            \\_                       /            `  `    \\\r\n" +
            "          /                \\                     |.                     \\\r\n" +
            "         /                   \\                   \\                       |\r\n" +
            "        /    .--._    .---.   |                   \\                      |\r\n" +
            "        |  /      -__-     \\   |                    ~-/--`-`-`-\\         |\r\n" +
            "        | |                |  |                     |          \\        |\r\n" +
            "         ||                  ||                     |            |       |\r\n" +
            "         ||     ,_   _.      ||                     |            |       |\r\n" +
            "         ||      e   e      ||  Hey Beavis,         |   _--    |       |\r\n" +
            "          ||     _  |_      ||   pull my finger!     _| =-.    |.-.    |\r\n" +
            "         @|     (o\\_/o)     |@   Heh,Heh!!!          o|/o/       _.   |\r\n" +
            "           |     _____     |                        /  ~          \\ |\r\n" +
            "            \\ ( /uuuuu\\ ) /             No way!    (/___@)  ___~    |\r\n" +
            "             \\  `====='  /              Ass wipe!!    |_===~~~.`    |\r\n" +
            "              \\  -___-  /                         _______.--~     |\r\n" +
            "               |       |            //             \\________       |\r\n" +
            "               /-_____-\\       .  _//_                      \\      |\r\n" +
            "             /           \\     \\\\/////                    __/-___-- -_\r\n" +
            "           /               \\    \\   /                    /            __\\\r\n" +
            "          /__|  AC / DC  |__\\   / /                      -| Metallica|| |\r\n" +
            "          | ||           |\\ \\  / /                       ||          || |\r\n" +
            "          | ||           | \\ \\/ /                        ||          || |\r\n";

    public static String VERSION = "0.1-SNAPSHOT";

    private final RoomManager roomManager;
    private final PlayerManager playerManager;
    private final ChannelUtils channelUtils;
    private final NewUserRegistrationManager newUserRegistrationManager;
    private final EntityManager entityManager;
    private final ItemDecayManager itemDecayManager;

    public GameManager(RoomManager roomManager, PlayerManager playerManager, EntityManager entityManager) {
        this.roomManager = roomManager;
        this.playerManager = playerManager;
        this.entityManager = entityManager;
        this.itemDecayManager = new ItemDecayManager(entityManager);
        this.entityManager.addEntity(itemDecayManager);
        this.newUserRegistrationManager = new NewUserRegistrationManager(playerManager);
        this.channelUtils = new ChannelUtils(getPlayerManager(), getRoomManager());

    }

    public NewUserRegistrationManager getNewUserRegistrationManager() {
        return newUserRegistrationManager;
    }

    public ChannelUtils getChannelUtils() {
        return channelUtils;
    }

    public ItemDecayManager getItemDecayManager() {
        return itemDecayManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    private static final Integer LOBBY_ID = 1;

    public Set<Player> getAllPlayers() {
        ImmutableSet.Builder<Player> builder = ImmutableSet.builder();
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Room room = next.getValue();
            Set<Player> presentPlayers = playerManager.getPresentPlayers(room);
            for (Player player : presentPlayers) {
                builder.add(player);
            }
        }
        return builder.build();
    }

    public void setPlayerAfk(String username) {
        Player playerByUsername = playerManager.getPlayerByUsername(username);
        Optional<Room> playerCurrentRoom = roomManager.getPlayerCurrentRoom(playerByUsername);
        playerCurrentRoom.get().getPresentPlayerIds().remove(playerByUsername.getPlayerId());
        playerCurrentRoom.get().addAfkPlayer(playerByUsername.getPlayerId());
    }

    public void movePlayer(Movement movement) {
        synchronized (Interners.newStrongInterner()) {
            Room sourceRoom = roomManager.getRoom(movement.getSourceRoomId());
            Room destinationRoom = roomManager.getRoom(movement.getDestinationRoomId());
            sourceRoom.removePresentPlayer(movement.getPlayer().getPlayerId());
            for (Player next : playerManager.getPresentPlayers(sourceRoom)) {
                StringBuilder sb = new StringBuilder();
                sb.append(movement.getPlayer().getPlayerName());
                sb.append(" ").append(movement.getRoomExitMessage());
                channelUtils.writeNoPrompt(next.getPlayerId(), sb.toString());
            }
            for (Player next : playerManager.getPresentPlayers(destinationRoom)) {
                channelUtils.writeNoPrompt(next.getPlayerId(), movement.getPlayer().getPlayerName() + " arrived.");
            }
            destinationRoom.addPresentPlayer(movement.getPlayer().getPlayerId());
        }
    }

    public void placePlayerInLobby(Player player) {
        Room room = roomManager.getRoom(LOBBY_ID);
        room.addPresentPlayer(player.getPlayerId());
        for (Player next : playerManager.getPresentPlayers(room)) {
            if (next.getPlayerId().equals(player.getPlayerId())) {
                continue;
            }
            channelUtils.write(next.getPlayerId(), player.getPlayerName() + " arrived.");
        }
    }

    private String getExits(Room room, Player player) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[ Exits: ");
        stringBuilder.append(BRIGHT_GREEN);
        if (!player.getReturnDirection().isPresent()) {
            player.setReturnDirection(Optional.of("-"));
        }
        if (room.getNorthId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("north")) {
                stringBuilder.append(GREEN);
                stringBuilder.append("North ");
                stringBuilder.append(BRIGHT_GREEN);
            } else {
                stringBuilder.append("North ");
            }
        }
        if (room.getSouthId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("south")) {
                stringBuilder.append(GREEN);
                stringBuilder.append("South ");
                stringBuilder.append(BRIGHT_GREEN);
            } else {
                stringBuilder.append("South ");
            }
        }
        if (room.getEastId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("east")) {
                stringBuilder.append(GREEN);
                stringBuilder.append("East ");
                stringBuilder.append(BRIGHT_GREEN);
            } else {
                stringBuilder.append("East ");
            }
        }
        if (room.getWestId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("west")) {
                stringBuilder.append(GREEN);
                stringBuilder.append("West ");
                stringBuilder.append(BRIGHT_GREEN);
            } else {
                stringBuilder.append("West ");
            }
        }
        if (room.getUpId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("up")) {
                stringBuilder.append(GREEN);
                stringBuilder.append("Up ");
                stringBuilder.append(BRIGHT_GREEN);
            } else {
                stringBuilder.append("Up ");
            }
        }
        if (room.getDownId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("down")) {
                stringBuilder.append(GREEN);
                stringBuilder.append("Down ");
                stringBuilder.append(BRIGHT_GREEN);
            } else {
                stringBuilder.append("Down ");
            }
        }
        stringBuilder.append(RESET).append("]\r\n");
        return stringBuilder.toString();
    }

    public void currentRoomLogic(String playerId) {
        Player player = playerManager.getPlayer(playerId);
        final Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        StringBuilder sb = new StringBuilder();
        sb.append(BRIGHT_GREEN);
        sb.append(playerCurrentRoom.getRoomTitle()).append("\r\n\r\n");
        sb.append(RESET);
        sb.append(WordUtils.wrap(playerCurrentRoom.getRoomDescription(), 80)).append("\r\n");

        if (playerCurrentRoom.getNorthId().isPresent()) {
            Room room = roomManager.getRoom(playerCurrentRoom.getNorthId().get());
            sb.append("North - ");
            if (room != null) {
                sb.append(room.getRoomTitle()).append("\r\n");
            }
        }
        if (playerCurrentRoom.getSouthId().isPresent()) {
            Room room = roomManager.getRoom(playerCurrentRoom.getSouthId().get());
            sb.append("South - ");
            if (room != null) {
                sb.append(room.getRoomTitle()).append("\r\n");
            }
        }
        if (playerCurrentRoom.getEastId().isPresent()) {
            Room room = roomManager.getRoom(playerCurrentRoom.getEastId().get());
            sb.append("East - ");
            if (room != null) {
                sb.append(room.getRoomTitle()).append("\r\n");
            }
        }
        if (playerCurrentRoom.getWestId().isPresent()) {
            Room room = roomManager.getRoom(playerCurrentRoom.getWestId().get());
            sb.append("West - ");
            if (room != null) {
                sb.append(room.getRoomTitle()).append("\r\n");
            }
        }
        if (playerCurrentRoom.getUpId().isPresent()) {
            Room room = roomManager.getRoom(playerCurrentRoom.getUpId().get());
            sb.append("Up - ");
            if (room != null) {
                sb.append(room.getRoomTitle()).append("\r\n");
            }
        }
        if (playerCurrentRoom.getDownId().isPresent()) {
            Room room = roomManager.getRoom(playerCurrentRoom.getDownId().get());
            sb.append("Down - ");
            if (room != null) {
                sb.append(room.getRoomTitle()).append("\r\n");
            }
        }
        sb.append(getExits(playerCurrentRoom, player));
        for (String searchPlayerId : playerCurrentRoom.getPresentPlayerIds()) {
            if (searchPlayerId.equals(player.getPlayerId())) {
                continue;
            }
            Player searchPlayer = playerManager.getPlayer(searchPlayerId);
            sb.append(searchPlayer.getPlayerName()).append(" is here.\r\n");
        }
        for (String npcId : playerCurrentRoom.getNpcIds()) {
            Npc npcEntity = entityManager.getNpcEntity(npcId);
            sb.append("A ").append(npcEntity.getColorName()).append(" is here.\r\n");
        }
        for (String itemId : playerCurrentRoom.getItemIds()) {
            Item itemEntity = entityManager.getItemEntity(itemId);
            if (itemEntity == null) {
                playerCurrentRoom.remotePresentItem(itemId);
                continue;
            }
            sb.append(entityManager.getItemEntity(itemId).getItemName()).append(" is on the ground.\r\n");
        }
        sb.append("\r\n");
        channelUtils.write(player.getPlayerId(), sb.toString());
    }

    public void currentRoomLogic(CreeperSession creeperSession, MessageEvent e) {
        final String player = playerManager.getPlayerByUsername(creeperSession.getUsername().get()).getPlayerId();
        currentRoomLogic(player);
    }

    public void placeItemInRoom(Integer roomId, String itemId) {
        roomManager.getRoom(roomId).addPresentItem(entityManager.getItemEntity(itemId).getItemId());
    }

    public void acquireItem(Player player, String itemId) {
        Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        playerCurrentRoom.getItemIds().remove(itemId);
        playerManager.addInventoryId(player.getPlayerId(), itemId);
        Item itemEntity = entityManager.getItemEntity(itemId);
        itemEntity.setWithPlayer(true);
        entityManager.addItem(itemEntity);
        channelUtils.write(player.getPlayerId(), "You acquired " + entityManager.getItemEntity(itemId).getItemName());
    }
}
