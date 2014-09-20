package com.comandante.creeper.server.command;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.managers.GameManager;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class DropCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("d", "drop");
    final static String description = "Drop an item";

    public DropCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (originalMessageParts.size() == 1) {
                channelUtils.write(playerId, "No item specified.");
                return;
            }
            originalMessageParts.remove(0);
            String itemTarget = Joiner.on(" ").join(originalMessageParts);
            for (String inventoryId : playerManager.getPlayerMetadata(playerId).getInventory()) {
                Item itemEntity = entityManager.getItemEntity(inventoryId);
                if (itemEntity.getItemTriggers().contains(itemTarget)) {
                    itemEntity.setWithPlayer(false);
                    gameManager.placeItemInRoom(currentRoom.getRoomId(), itemEntity.getItemId());
                    playerMetadata.removeInventoryEntityId(itemEntity.getItemId());
                    playerManager.savePlayerMetadata(playerMetadata);
                    gameManager.getItemDecayManager().addItem(itemEntity);
                    entityManager.addItem(itemEntity);
                    gameManager.roomSay(currentRoom.getRoomId(), player.getPlayerName() + " dropped " + itemEntity.getItemName(), playerId);
                    return;
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
