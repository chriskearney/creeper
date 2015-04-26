package com.comandante.creeper.server.command.admin;


import com.comandante.creeper.fight.FightResults;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.server.Color;
import com.comandante.creeper.server.command.Command;
import com.comandante.creeper.world.Room;
import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class TeleportCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("teleport", "t");
    final static String description = "Teleport to a roomId or playerId. For admins only.";
    final static boolean isAdminOnly = true;

    public TeleportCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (originalMessageParts.size() <= 1) {
                return;
            }
            String desiredId = originalMessageParts.get(1);
            Iterator<Map.Entry<String, Player>> players = playerManager.getPlayers();
            while (players.hasNext()) {
                Map.Entry<String, Player> next = players.next();
                if (next.getValue().getPlayerName().equals(desiredId)) {
                    Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(next.getValue()).get();
                    PlayerMovement playerMovement = new PlayerMovement(player, gameManager.getRoomManager().getPlayerCurrentRoom(player).get().getRoomId(), playerCurrentRoom.getRoomId(), null, "vanished into the heavens.", "");
                    gameManager.movePlayer(playerMovement);
                    channelUtils.writeToRoom(playerId, "A " + Color.YELLOW + "lightning" + Color.RESET + " bolt descends from the sky and annihilates the earth below." + "\r\n");
                    gameManager.currentRoomLogic(player.getPlayerId());
                    return;
                }
            }
            Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
            while (rooms.hasNext()) {
                Map.Entry<Integer, Room> next = rooms.next();
                if (Integer.toString(next.getKey()).equals(desiredId)) {
                    PlayerMovement playerMovement = new PlayerMovement(player, gameManager.getRoomManager().getPlayerCurrentRoom(player).get().getRoomId(), Integer.parseInt(desiredId), null, "vanished into the heavens.", "");
                    gameManager.movePlayer(playerMovement);
                    channelUtils.writeToRoom(playerId, "A " + Color.YELLOW + "lightning" + Color.RESET + " bolt descends from the sky and annihilates the earth below." + "\r\n");
                    gameManager.currentRoomLogic(player.getPlayerId());
                    return;
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}