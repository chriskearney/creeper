package com.comandante.creeper.server.command;


import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.Room;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MapCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("m", "map");
    final static String description = "Display the map.";

    public MapCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        Player player = playerManager.getPlayer(playerId);
        final Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        StringBuilder sb = new StringBuilder();
        try {
            if (playerCurrentRoom.getMapData().isPresent()) {
                sb.append(playerCurrentRoom.getMapData().get()).append("\r\n");
                channelUtils.write(player.getPlayerId(), sb.toString());
            } else {
                channelUtils.write(player.getPlayerId(), "No map data.");
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}

