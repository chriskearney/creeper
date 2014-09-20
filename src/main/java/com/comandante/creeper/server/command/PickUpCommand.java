package com.comandante.creeper.server.command;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.managers.GameManager;
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
        configure(e);
        try {
            Set<String> itemIds = currentRoom.getItemIds();
            originalMessageParts.remove(0);
            String desiredPickUpItem = Joiner.on(" ").join(originalMessageParts);
            for (String next : itemIds) {
                Item itemEntity = entityManager.getItemEntity(next);
                if (itemEntity.getItemTriggers().contains(desiredPickUpItem)) {
                    gameManager.acquireItem(player, itemEntity.getItemId());
                    String playerName = player.getPlayerName();
                    gameManager.roomSay(currentRoom.getRoomId(), playerName + " picked up " + itemEntity.getItemName(), playerId);
                    return;
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
