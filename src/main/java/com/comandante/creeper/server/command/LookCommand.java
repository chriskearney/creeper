package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LookCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("l", "look");
    final static String description = "Examine your surroundings.";

    public LookCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession creeperSession = extractCreeperSession(e.getChannel());
            List<String> originalMessageParts = getOriginalMessageParts(e);
            if (originalMessageParts.size() == 1) {
                getGameManager().currentRoomLogic(getPlayerId(creeperSession));
                return;
            }
            originalMessageParts.remove(0);
            String target = Joiner.on(" ").join(originalMessageParts);
            //Players
            Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId(creeperSession));
            Room playerCurrentRoom = getGameManager().getRoomManager().getPlayerCurrentRoom(player).get();
            Set<String> presentPlayerIds = playerCurrentRoom.getPresentPlayerIds();
            for (String presentPlayerId : presentPlayerIds) {
                Player presentPlayer = getGameManager().getPlayerManager().getPlayer(presentPlayerId);
                if (presentPlayer.getPlayerName().equals(target)) {
                    getGameManager().getChannelUtils().write(getPlayerId(creeperSession),
                            getGameManager().getPlayerManager().getLookString(player));
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
