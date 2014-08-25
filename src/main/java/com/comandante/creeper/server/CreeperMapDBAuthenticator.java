package com.comandante.creeper.server;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import org.jboss.netty.channel.Channel;

public class CreeperMapDBAuthenticator implements CreeperAuthenticator {

    private final GameManager gameManager;

    public CreeperMapDBAuthenticator(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean authenticateAndRegisterPlayer(String username, String password, Channel channel) {
        PlayerMetadata playerMetadata = gameManager.getPlayerManager().getPlayerMetadata(new Player(username).getPlayerId());
        if (playerMetadata == null) {
            return false;
        }
        if (!playerMetadata.getPassword().equals(password)) {
            return false;
        }
        if (gameManager.getPlayerManager().doesPlayerExist(username)) {
            gameManager.getPlayerManager().removePlayer(username);
        }
        Player player = new Player(username);
        player.setChannel(channel);
        gameManager.getPlayerManager().addPlayer(player);
        if (!gameManager.getRoomManager().getPlayerCurrentRoom(player).isPresent()) {
            gameManager.placePlayerInLobby(player);
        }
        return true;
    }
}
