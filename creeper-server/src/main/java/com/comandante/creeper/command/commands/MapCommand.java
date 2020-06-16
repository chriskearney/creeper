package com.comandante.creeper.command.commands;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.model.Coords;
import com.comandante.creeper.world.model.Room;
import org.apache.commons.lang.math.NumberUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MapCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("map", "m");
    final static String description = "Display the map.";
    final static String correctUsage = "map <size number>";


    public MapCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() > 1 && NumberUtils.isNumber(originalMessageParts.get(1))) {
                int max = Integer.parseInt(originalMessageParts.get(1));
                write(mapsManager.drawMap(currentRoom.getRoomId(), new Coords(max, max)));
            } else {
                Player player1 = playerManager.getPlayer(playerId);
                final Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player1).get();
                StringBuilder sb = new StringBuilder();

                if (playerCurrentRoom.getMapData().isPresent()) {
                    sb.append(playerCurrentRoom.getMapData().get()).append("\r\n");
                    channelUtils.write(player1.getPlayerId(), sb.toString());
                } else {
                    channelUtils.write(player1.getPlayerId(), "You are in an uncharted location.");
                }
            }
        });
    }
}

