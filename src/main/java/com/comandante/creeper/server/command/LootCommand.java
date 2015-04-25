package com.comandante.creeper.server.command;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.Loot;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.Color;
import com.comandante.creeper.world.Coords;
import com.comandante.creeper.world.Room;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class LootCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("loot");
    final static String description = "Loot a corpse.";

    public LootCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (originalMessageParts.size() > 1) {
                for (String inventoryString: playerMetadata.getInventory()) {
                    Item itemEntity = entityManager.getItemEntity(inventoryString);
                    if (itemEntity != null) {
                        if (itemEntity.getItemTypeId() == Item.CORPSE_ID_RESERVED) {
                            Loot loot = itemEntity.getLoot();
                            if (loot != null) {
                                if (loot.getLootGold() > 0) {
                                    write("You looted " + loot.getLootGold() + Color.YELLOW + " gold." + Color.RESET);
                                    playerManager.incrementGold(playerId, loot.getLootGold());
                                }
                            }
                            playerManager.removeInventoryId(playerId, itemEntity.getItemId());
                            entityManager.removeItem(itemEntity);
                            return;
                        }
                    }
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}