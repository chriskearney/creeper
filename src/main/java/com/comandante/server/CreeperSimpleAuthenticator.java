package com.comandante.server;

import com.comandante.managers.GameManager;
import com.comandante.model.Player;
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
        userMap.put("test1", "poop");
        userMap.put("test2", "poop");
        userMap.put("test3", "poop");
        userMap.put("test4", "poop");
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
        if (!gameManager.getPlayerCurrentRoom(player).isPresent()) {
            gameManager.placePlayerInLobby(player);
        }
        gameManager.getPlayerManager().addPlayer(player);
        return true;
    }
}
