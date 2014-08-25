package com.comandante.creeper.server;

import com.comandante.creeper.managers.PlayerManager;
import com.comandante.creeper.managers.RoomManager;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
import org.jboss.netty.channel.Channel;

public class ChannelUtils {

    private final PlayerManager playerManager;
    private final RoomManager roomManager;

    public ChannelUtils(PlayerManager playerManager, RoomManager roomManager) {
        this.playerManager = playerManager;
        this.roomManager = roomManager;
    }

    public void write(String playerId, String message) {
        Player player = playerManager.getPlayer(playerId);
        Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        Channel channel = player.getChannel();
        channel.write(message);
        channel.write(playerManager.getPrompt(playerId, playerCurrentRoom.getRoomId()));
    }
}

