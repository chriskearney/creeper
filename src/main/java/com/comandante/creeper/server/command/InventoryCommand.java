package com.comandante.creeper.server.command;


import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.server.CreeperSession;
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
        try {
            CreeperSession session = extractCreeperSession(e.getChannel());
            PlayerMetadata playerMetadata = getGameManager().getPlayerManager().getPlayerMetadata(getPlayerId(session));
            String[] inventory1 = playerMetadata.getInventory();
            if (inventory1 == null) {
                getGameManager().getChannelUtils().write(getPlayerId(session), "You aren't carrying anything.");
                return;
            }
            ArrayList<String> inventory = new ArrayList<String>(Arrays.asList(playerMetadata.getInventory()));
            StringBuilder sb = new StringBuilder();
            sb.append("You are carrying:\r\n");
            sb.append(RESET);
            for (String inventoryId : inventory) {
                Item item = getGameManager().getEntityManager().getItemEntity(inventoryId);
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
                sb.append("\r\n");
            }
            sb.append("\r\n");
            getGameManager().getChannelUtils().write(getPlayerId(session), sb.toString());
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
