package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.CreeperSession;
import com.comandante.creeper.server.command.Command;
import com.comandante.creeper.world.Room;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class BuildCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("build", "b");
    final static String description = "Saves the current world to disk.";
    final static boolean isAdminOnly = true;

    public BuildCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            //
            CreeperSession session = extractCreeperSession(e.getChannel());
            String playerId = extractPlayerId(session);
            GameManager gameManager = getGameManager();
            Player player = gameManager.getPlayerManager().getPlayer(playerId);
            ChannelUtils utils = gameManager.getChannelUtils();
            List<String> originalMessageParts = getOriginalMessageParts(e);
            Room currentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
            if (originalMessageParts.size() == 1) {
                utils.write(playerId, "You must specify a direction in which to build.");
                return;
            }
            String desiredBuildDirection = originalMessageParts.get(1);
            if (desiredBuildDirection.equalsIgnoreCase("n") | desiredBuildDirection.equalsIgnoreCase("north")) {
                if (!currentRoom.getNorthId().isPresent()) {

                } else {
                    utils.write(playerId, "Error!  There is already a room to the North.");
                }

            } else if (desiredBuildDirection.equalsIgnoreCase("s") | desiredBuildDirection.equalsIgnoreCase("south")) {

            } else if (desiredBuildDirection.equalsIgnoreCase("e") | desiredBuildDirection.equalsIgnoreCase("east")) {

            } else if (desiredBuildDirection.equalsIgnoreCase("w") | desiredBuildDirection.equalsIgnoreCase("west")) {

            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
