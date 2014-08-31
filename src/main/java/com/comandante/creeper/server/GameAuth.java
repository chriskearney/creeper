package com.comandante.creeper.server;

import com.comandante.creeper.Main;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import org.jboss.netty.channel.Channel;

public class GameAuth implements CreeperAuthenticator {

    private final GameManager gameManager;

    public GameAuth(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean authenticateAndRegisterPlayer(String username, String password, Channel channel) {
        PlayerMetadata playerMetadata = gameManager.getPlayerManager().getPlayerMetadata(Main.createPlayerId(username));
        if (playerMetadata == null) {
            return false;
        }
        if (!playerMetadata.getPassword().equals(password)) {
            return false;
        }
        if (gameManager.getPlayerManager().doesPlayerExist(username)) {
            gameManager.getPlayerManager().removePlayer(username);
        }
        Player player = new Player(username, gameManager);
        player.setChannel(channel);
        gameManager.getPlayerManager().addPlayer(player);
        if (!gameManager.getRoomManager().getPlayerCurrentRoom(player).isPresent()) {
            gameManager.placePlayerInLobby(player);
        }
        return true;
    }
}
