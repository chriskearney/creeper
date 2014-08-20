package com.comandante.creeper.server;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import org.jboss.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class CreeperSimpleAuthenticator implements CreeperAuthenticator {

    GameManager gameManager;

    public CreeperSimpleAuthenticator(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private static final Map<String, String> userMap;
    static {
        userMap = new HashMap<String, String>();
        userMap.put("chris", "poop");
        userMap.put("brian", "poop");
        userMap.put("sean", "poop");
    }

    @Override
    public boolean authenticateAndRegisterPlayer(String userName, String passWord, Channel channel) {
        String userPassword = userMap.get(userName);
        if (userPassword == null) {
            return false;
        }
        if (!userPassword.equals(passWord)) {
            return false;
        }
        if (gameManager.getPlayerManager().doesPlayerExist(userName)) {
            gameManager.getPlayerManager().removePlayer(userName);
        }
        Player player = new Player(userName);
        player.setChannel(channel);
        gameManager.getPlayerManager().addPlayer(player);
        if (!gameManager.getPlayerCurrentRoom(player).isPresent()) {
            gameManager.placePlayerInLobby(player);
        }
        return true;
    }
}
