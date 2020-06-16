package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.world.model.Room;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BackCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("back", "b");
    final static String description = "Return to where you came from.";
    final static String correctUsage = "back";

    public BackCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (player.isActiveFights()) {
                write("You can't move while in a fight!");
                return;
            }
            Optional<Room> returnRoom = player.getPreviousRoom();
            if (!returnRoom.isPresent()) {
                write("I don't know where you came from.");
                return;
            }
            PlayerMovement playerMovement = new PlayerMovement(player, currentRoom.getRoomId(), returnRoom.get().getRoomId(), "returned to where they came from.");
            player.movePlayer(playerMovement);
        });
    }

}