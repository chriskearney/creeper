package com.comandante.creeper.merchant.lockers;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Item;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class PutCommand extends LockerCommand {

    final static List<String> validTriggers = Arrays.asList("put", "p");
    final static String description = "Put an item in your locker.";

    public PutCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            configure(e);
            originalMessageParts.remove(0);
            String desiredDropOffItem = Joiner.on(" ").join(originalMessageParts);
            for (Item item : player.getInventory()) {
                if (item.getItemTriggers().contains(desiredDropOffItem)) {
                    item.setWithPlayer(false);
                    player.transferItemToLocker(item.getItemId());
                    gameManager.getEntityManager().saveItem(item);
                    write(item.getItemName() + " has been transfered to your locker.\r\n");
                    return;
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}