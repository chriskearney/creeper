package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.player.CoolDownType;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.world.model.RemoteExit;
import com.comandante.creeper.world.model.Room;
import com.google.common.collect.ImmutableList;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Optional;

public class MovementCommand extends Command {

    final static String description = "Move your player.";
    final static String correctUsage = "n|s|e|w|enter e-<name>";

    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().addAll(Room.Direction.allTriggers).build();

    public MovementCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        this.execCommand(ctx, e, () -> {
            final String command = rootCommand;
            Optional<Room.Direction> direction = Room.Direction.from(command.toLowerCase());
            if (!direction.isPresent()) {
                throw new RuntimeException("Unable to retrieve direction.");
            }
            if (direction.get().equals(Room.Direction.ENTER)) {
                direction.get().setRemoteExit(doesEnterExitExist());
            }
            Optional<PlayerMovement> playerMovement = currentRoom.derivePlayerMovement(player, direction.get());

            if (!playerMovement.isPresent()) {
                MovementCommand.this.write("There's no exit in that direction. (" + command + ")");
                return;
            }

            player.movePlayer(playerMovement.get());
        });
    }

    private java.util.Optional<RemoteExit> doesEnterExitExist() {
        if (originalMessageParts.size() > 1) {
            String enterExitName = originalMessageParts.get(1);
            for (RemoteExit remoteExit : currentRoom.getEnterExits()) {
                if (remoteExit.getExitDetail().equalsIgnoreCase(enterExitName)) {
                    return Optional.of(remoteExit);
                }
            }
        }
        return Optional.empty();
    }
}
