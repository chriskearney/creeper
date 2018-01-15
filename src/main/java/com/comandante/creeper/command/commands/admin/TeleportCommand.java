package com.comandante.creeper.command.commands.admin;


import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.player.CoolDownType;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.world.model.Room;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.*;

public class TeleportCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("teleport", "t");
    final static String description = "Teleport to a roomId or playerId.";
    final static String correctUsage = "teleport <roomId|playerName>";
//    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);


    final static String teleportMessage = "A " + Color.YELLOW + "lightning" + Color.RESET + " bolt descends from the sky and annihilates the earth below." + "\r\n";

    public TeleportCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() <= 1) {
                return;
            }
            if (player.isActiveFights()) {
                write("You can't teleport while in a fight!");
                return;
            }
            if (player.isActive(CoolDownType.DEATH)) {
                write("You are dead and can not move.");
                return;
            }
            Optional<PlayerMetadata> playerMetadataOptional = playerManager.getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            for (Effect effect : playerMetadata.getEffects()) {
                if (effect.isFrozenMovement()) {
                    write("You are frozen and can not move.");
                    return;
                }
            }
            String desiredId = originalMessageParts.get(1);
            Iterator<Map.Entry<String, Player>> players = playerManager.getPlayers();
            while (players.hasNext()) {
                Map.Entry<String, Player> next = players.next();
                if (next.getValue().getPlayerName().equals(desiredId)) {
                    Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(next.getValue()).get();
                    Integer destinationRoomId = playerCurrentRoom.getRoomId();
                    PlayerMovement playerMovement = new PlayerMovement(player, gameManager.getRoomManager().getPlayerCurrentRoom(player).get().getRoomId(), playerCurrentRoom.getRoomId(), "vanished into the heavens.", "");
                    gameManager.writeToRoom(destinationRoomId, teleportMessage);
                    channelUtils.write(playerId, teleportMessage);
                    player.movePlayer(playerMovement);
                    gameManager.currentRoomLogic(player.getPlayerId());
                    return;
                }
            }
            Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRoomsIterator();
            while (rooms.hasNext()) {
                Map.Entry<Integer, Room> next = rooms.next();
                if (Integer.toString(next.getKey()).equals(desiredId)) {
                    PlayerMovement playerMovement = new PlayerMovement(player, gameManager.getRoomManager().getPlayerCurrentRoom(player).get().getRoomId(), Integer.parseInt(desiredId), "vanished into the heavens.", "");
                    gameManager.writeToRoom(Integer.parseInt(desiredId), teleportMessage);
                    channelUtils.write(playerId, teleportMessage);
                    player.movePlayer(playerMovement);
                    gameManager.currentRoomLogic(player.getPlayerId());
                    return;
                }
            }
        });
    }
}