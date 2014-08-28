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

    public static String LOGO = "                      _    _  __  _  _ _____  ___ ___\r\n" +
            "                      | /\\ | |__| |\\ |   |   |___ |  \\\r\n" +
            "                      |/  \\| |  | | \\|   |   |___ |__/\r\n" +
            "\r\n" +
            "         F O R   C R I M E S   A G A I N S T   T H E   E M P I R E\r\n" +
            " ________________________  _________________________  _______________________\r\n" +
            "|        .......         ||      .x%%%%%%x.         ||  ,.------;:~~:-.      |\r\n" +
            "|      ::::::;;::.       ||     ,%%%%%%%%%%%        || /:.\\`;;|||;:/;;:\\     |\r\n" +
            "|    .::;::::;::::.      ||    ,%%%'  )'  \\%        ||:')\\`:\\||::/.-_':/)    |\r\n" +
            "|   .::::::::::::::      ||   ,%x%) __   _ Y        ||`:`\\\\\\ ;'||'.''/,.:\\   |\r\n" +
            "|   ::`_```_```;:::.     ||   :%%% ~=-. <=~|        ||==`;.:`|;::/'/./';;=   |\r\n" +
            "|   ::=-) :=-`  ::::     ||   :%%::. .:,\\  |        ||:-/-%%% | |%%%;;_- _:  |\r\n" +
            "| `::|  / :     `:::     ||   `;%:`\\. `-' .'        ||=// %wm)..(mw%`_ :`:\\  |\r\n" +
            "|   '|  `~'     ;:::     ||    ``x`. -===-;         ||;;--', /88\\ -,- :-~~|  |\r\n" +
            "|    :-:==-.   / :'      ||     / `:`.__.;          ||-;~~::'`~^~:`::`/`-=:) |\r\n" +
            "|    `. _    .'.d8:      ||  .d8b.  :: ..`.         ||(;':)%%%' `%%%.`:``:)\\ |\r\n" +
            "| _.  |88bood88888._     || d88888b.  '  /8         ||(\\ %%%/dV##Vb`%%%%:`-. |\r\n" +
            "|~  `-+8888888888P  `-. _||d888888888b. ( 8b       /|| |);/( ;~~~~ :)\\`;;.``\\|\r\n" +
            "|-'     ~~^^^^~~  `./8 ~ ||~   ~`888888b  `8b     /:|| //\\'/,/|;;|:(: |.|\\;|\\|\r\n" +
            "|8b /  /  |   \\  \\  `8   ||  ' ' `888888   `8. _ /:/||/) |(/ | / \\|\\\\`( )- ` |\r\n" +
            "|P        `          8   ||'      )88888b   8b |):X ||;):):)/.):|/) (`:`\\\\`-`|\r\n" +
            "|                    8b  ||   ~ - |888888   `8b/:/:\\||;%/ //;/(\\`.':| ::`\\\\;`|\r\n" +
            "|                    `8  ||       |888888    88\\/~~;||;/~( \\|./;)|.|):;\\. \\\\-|\r\n" +
            "|                     8b ||       (888888b   88|  / ||/',:\\//) ||`.|| (:\\)):%|\r\n" +
            "| LS      .           `8 ||\\       \\888888   8-:   /||,|/;/(%;.||| (|(\\:- ; :|\r\n" +
            "|________/_\\___________8_||_\\_______\\88888_.'___\\__/||_%__%:__;_:`_;_:_.\\%_`_|\r\n" +
            "L u k e  S k y w a l k e r      H a n   S o l o          C h e w b a c c a\r\n" +
            "Self-Proclaimed Jedi Knight     Smuggler, Pirate         Smuggler, Pirate\r\n" +
            "     500,000 credits            200,000 Credits          100,000 credits\r\n";

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
        stringBuilder.append("[ ");
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

        sb.append(getExits(playerCurrentRoom, player));
        for (String searchPlayerId : playerCurrentRoom.getPresentPlayerIds()) {
            if (searchPlayerId.equals(player.getPlayerId())) {
                continue;
            }
            Player searchPlayer = playerManager.getPlayer(searchPlayerId);
            sb.append(searchPlayer.getPlayerName()).append(" is here.\r\n").append(RESET);
        }

        for (String itemId : playerCurrentRoom.getItemIds()) {
            Item itemEntity = entityManager.getItemEntity(itemId);
            if (itemEntity == null) {
                playerCurrentRoom.remotePresentItem(itemId);
                continue;
            }
            sb.append("   ").append(entityManager.getItemEntity(itemId).getRestingName()).append("\r\n");
        }

        for (String npcId : playerCurrentRoom.getNpcIds()) {
            Npc npcEntity = entityManager.getNpcEntity(npcId);
            sb.append("a ").append(npcEntity.getColorName()).append(" is here.\r\n");
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
    }

}
