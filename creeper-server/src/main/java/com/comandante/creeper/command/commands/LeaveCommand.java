package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.world.model.RemoteExit;
import com.comandante.creeper.world.model.Room;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class LeaveCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("leave");
    final static String description = "Enters a Leave exit";
    final static String correctUsage = "leave";

    public LeaveCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            PlayerMovement playerMovement;
            List<RemoteExit> leave = currentRoom.getEnterExits().stream().filter(remoteExit -> remoteExit.getExitDetail().equalsIgnoreCase("leave")).collect(Collectors.toList());
            if (leave.size() > 0) {
                Room destinationRoom = roomManager.getRoom(leave.get(0).getRoomId());
                playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), destinationRoom.getRoomId(), "entered " + leave.get(0).getExitDetail() + ".");
                player.movePlayer(playerMovement);
                return;
            }
            write("There is no Leave exit." + "\r\n");
        });
    }
}