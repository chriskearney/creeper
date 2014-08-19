package com.comandante;

import org.jboss.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class SimpleGameAuthenticator implements GameAuthenticator {

    GameManager gameManager;

    public SimpleGameAuthenticator(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private static final Map<String, String> userMap;
    static {
        userMap = new HashMap<String, String>();
        userMap.put("chris", "poop");
        userMap.put("brian", "poop");
    }

    @Override
    public boolean authenticatePlayer(String userName, String passWord, Channel channel) {
        String userPassword = userMap.get(userName);
        if (userPassword == null) {
            return false;
        }
        if (!userPassword.equals(passWord)) {
            return false;
        }
        Player player = new Player(userName);
        if (gameManager.doesPlayerExist(player)) {
            gameManager.removePlayer(player);
        }
        player.setChannel(channel);
        gameManager.addPlayer(player);
        return true;
    }
}
