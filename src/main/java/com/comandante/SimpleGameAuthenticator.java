package com.comandante;

import org.jboss.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

public class SimpleGameAuthenticator implements GameAuthenticator {

    RoomManager roomManager;

    public SimpleGameAuthenticator(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    private static final Map<String, String> userMap;
    static {
        userMap = new HashMap<String, String>();
        userMap.put("chris", "poop");
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
        if (roomManager.doesPlayerExist(player)) {
            roomManager.removePlayer(player);
        }
        player.setChannel(channel);
        roomManager.addPlayer(player);
        return true;
    }
}
