package com.comandante.creeper.server.command;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.room.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PickUpCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("p", "pick", "pickup");
    final static String description = "Pick up an item.";

    public PickUpCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            List<String> originalMessageParts = getOriginalMessageParts(e);
            CreeperSession session = getCreeperSession(e.getChannel());
            Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId(session));
            Room playerCurrentRoom = getGameManager().getRoomManager().getPlayerCurrentRoom(player).get();
            Set<String> itemIds = playerCurrentRoom.getItemIds();
            originalMessageParts.remove(0);
            String desiredPickUpItem = Joiner.on(" ").join(originalMessageParts);
            for (String next : itemIds) {
                Item itemEntity = getGameManager().getEntityManager().getItemEntity(next);
                if (itemEntity.getItemTriggers().contains(desiredPickUpItem)) {
                    getGameManager().acquireItem(player, itemEntity.getItemId());
                    getGameManager().roomSay(playerCurrentRoom.getRoomId(), getGameManager().getPlayerManager().getPlayer(getPlayerId(session)).getPlayerName() + " picked up " + itemEntity.getItemName(), getPlayerId(session));
                    return;
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
