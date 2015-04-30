package com.comandante.creeper.command;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.Items.ItemUseHandler;
import com.comandante.creeper.managers.GameManager;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class EquipCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("equip");
    final static String description = "Equip an item.";

    public EquipCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (originalMessageParts.size() == 1) {
                write("No equipment item specified.");
                return;
            }
            originalMessageParts.remove(0);
            String itemTarget = Joiner.on(" ").join(originalMessageParts);
            if (playerMetadata.getInventory() != null) {
                for (String inventoryId : playerMetadata.getInventory()) {
                    Item itemEntity = entityManager.getItemEntity(inventoryId);
                    if (itemEntity.getItemTriggers().contains(itemTarget)) {
                        if (itemEntity.getEquipment() == null) {
                            write("Item is not equipable.");
                            return;
                        }
                        equipmentManager.equip(playerMetadata, itemEntity);
                        return;
                    }
                }
            } else {
                write("Your inventory is empty.");
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}