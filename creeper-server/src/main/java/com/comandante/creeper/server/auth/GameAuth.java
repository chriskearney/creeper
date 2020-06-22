package com.comandante.creeper.server.auth;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.chat.Gossip;
import com.comandante.creeper.chat.Users;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.world.model.Room;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.Map;
import java.util.Optional;

public class GameAuth implements CreeperAuthenticator {

    private final GameManager gameManager;
    private final ObjectMapper objectMapper;
    private static final Logger log = Logger.getLogger(GameAuth.class);

    public GameAuth(ObjectMapper objectMapper, GameManager gameManager) {
        this.gameManager = gameManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean authenticateAndRegisterPlayer(String username, String password, Channel channel) {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(Creeper.createPlayerId(username));
        if (!playerMetadataOptional.isPresent()) {
            return false;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        if (!playerMetadata.getPassword().equals(password)) {
            return false;
        }
        Room currentRoom = null;
        if (gameManager.getPlayerManager().doesPlayerExist(username)) {
            currentRoom = gameManager.getPlayerManager().getPlayerByUsername(username).getCurrentRoom();
            gameManager.getPlayerManager().removePlayer(username);
        } else {
            Optional<Room> playerCurrentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(Creeper.createPlayerId(username));
            if (playerCurrentRoom.isPresent()) {
                currentRoom = playerCurrentRoom.get();
            }
        }

        Player player = new Player(username, gameManager);
        if (currentRoom != null) {
            player.setCurrentRoomAndPersist(currentRoom);
            currentRoom.addPresentPlayer(player.getPlayerId());
        } else {
            currentRoom = player.getCurrentRoom();
            if (currentRoom != null) {
                currentRoom.addPresentPlayer(player.getPlayerId());
                player.setCurrentRoom(player.getCurrentRoom());
            }
        }
        player.setChannel(channel);
        gameManager.getPlayerManager().addPlayer(player);
        if (currentRoom == null) {
            gameManager.placePlayerInLobby(player);
        }

        gameManager.emitUsersEvent(player.getPlayerId());

        return true;
    }
}
