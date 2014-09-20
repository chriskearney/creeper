package com.comandante.creeper.server.command;


import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.managers.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.comandante.creeper.server.Color.RESET;

public class InventoryCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("i", "inventory");
    final static String description = "View your inventory.";

    public InventoryCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            String[] inventory1 = playerMetadata.getInventory();
            if (inventory1 == null) {
                write("You aren't carrying anything.");
                return;
            }
            ArrayList<String> inventory = new ArrayList<String>(Arrays.asList(playerMetadata.getInventory()));
            StringBuilder sb = new StringBuilder();
            sb.append("You are carrying:\r\n");
            sb.append(RESET);
            for (String inventoryId : inventory) {
                Item item = entityManager.getItemEntity(inventoryId);
                sb.append(item.getItemName());
                int maxUses = ItemType.itemTypeFromCode(item.getItemTypeId()).getMaxUses();
                if (maxUses > 0) {
                    int remainingUses = maxUses - item.getNumberOfUses();
                    sb.append(" - ").append(remainingUses);
                    if (remainingUses == 1) {
                        sb.append(" use left.");
                    } else {
                        sb.append(" uses left.");
                    }
                }
            }
            write(sb.toString());
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
