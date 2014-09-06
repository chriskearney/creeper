package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.server.command.Command;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TagRoomCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("tr", "tagRoom");
    final static String description = "Sets a tag on a world.";
    final static boolean isAdminOnly = true;

    public TagRoomCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            GameManager gameManager = getGameManager();
            Player player = gameManager.getPlayerManager().getPlayer(getPlayerId(extractCreeperSession(e.getChannel())));
            Room playerCurrentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
            List<String> originalMessageParts = getOriginalMessageParts(e);
            originalMessageParts.remove(0);
            if (originalMessageParts.get(0).equalsIgnoreCase("list")) {
                StringBuilder sb = new StringBuilder();
                Iterator<String> iterator = playerCurrentRoom.getRoomTags().iterator();
                while (iterator.hasNext()) {
                    String tag = iterator.next();
                    sb.append(tag).append("\n");
                }
                gameManager.getChannelUtils().write(player.getPlayerId(), "tag\n---");
                gameManager.getChannelUtils().write(player.getPlayerId(), sb.toString());
                return;
            }
            playerCurrentRoom.addTag(originalMessageParts.get(0));
            gameManager.getChannelUtils().write(player.getPlayerId(), String.format("tagged world with tag: \"%s\".", originalMessageParts.get(0)));
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
