package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.room.Room;
import com.comandante.creeper.server.CreeperSession;
import com.comandante.creeper.server.command.Command;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class TitleCommand extends Command {


    final static List<String> validTriggers = Arrays.asList("title");
    final static String description = "Set the title. For admins only.";
    final static boolean isAdminOnly = true;

    public TitleCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            GameManager gameManager = getGameManager();
            List<String> originalMessageParts = getOriginalMessageParts(e);
            originalMessageParts.remove(0);
            String join = Joiner.on(" ").join(originalMessageParts);
            CreeperSession session = extractCreeperSession(e.getChannel());
            Player player = gameManager.getPlayerManager().getPlayer(getPlayerId(session));
            Room playerCurrentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(player).get();
            playerCurrentRoom.setRoomTitle(join);
            gameManager.getChannelUtils().write(player.getPlayerId(), "Titled saved.");
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
