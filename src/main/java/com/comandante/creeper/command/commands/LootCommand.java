package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemBuilder;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.items.Loot;
import com.comandante.creeper.server.player_communication.Color;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class LootCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("loot");
    final static String description = "Loot a corpse.";
    final static String correctUsage = "loot corpse";

    private final GameManager gameManager;

    public LootCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
        this.gameManager = gameManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() > 1) {
                for (Item item : player.getInventory()) {
                    if (item.getInternalItemName().equals(Item.CORPSE_INTENAL_NAME)) {
                        Loot loot = item.getLoot();
                        if (loot != null) {

                            // Stop now and wrap the loot in a chest.
                            if (loot.getChestInternalItemName() != null) {
                                Optional<ItemMetadata> itemMetadata = gameManager.getItemStorage().get(loot.getChestInternalItemName());
                                if (itemMetadata.isPresent()) {

                                    Loot newLoot = new Loot();
                                    newLoot.setItems(loot.getInternalItemNames());
                                    newLoot.setLootGoldMax(loot.getLootGoldMax());
                                    newLoot.setLootGoldMin(loot.getLootGoldMin());

                                    Item newChest = new ItemBuilder()
                                            .from(itemMetadata.get())
                                            .loot(newLoot)
                                            .create();

                                    gameManager.getEntityManager().saveItem(newChest);
                                    gameManager.acquireItem(player, newChest.getItemId(), true);
                                    write("You looted " + newChest.getItemName() + " from a " + item.getItemName() + ".\r\n");
                                    player.removeInventoryId(item.getItemId());
                                    entityManager.removeItem(item);
                                    return;
                                }
                            }

                            Optional<Item> chestItem = wrapInChestIfNeeded(loot);
                            if (chestItem.isPresent()) {
                                gameManager.acquireItem(player, chestItem.get().getItemId(), true);
                                write("You looted " + chestItem.get().getItemName() + " from a " + item.getItemName() + ".\r\n");
                                player.removeInventoryId(item.getItemId());
                                entityManager.removeItem(item);
                                return;
                            }

                            long gold = lootManager.lootGoldAmountReturn(loot);
                            if (gold > 0) {
                                write("You looted " + NumberFormat.getNumberInstance(Locale.US).format(gold) + Color.YELLOW + " gold" + Color.RESET + " from a " + item.getItemName() + ".\r\n");
                                player.incrementGold(gold);
                            }
                            Set<Item> items = lootManager.lootItemsReturn(loot);
                            for (Item i : items) {
                                gameManager.acquireItem(player, i.getItemId(), true);
                                write("You looted " + i.getItemName() + " from a " + item.getItemName() + ".\r\n");
                            }
                            if (gold <= 0 && items.size() == 0) {
                                write("You looted nothing from " + item.getItemName() + "\r\n");
                            }

                        }
                        player.removeInventoryId(item.getItemId());
                        entityManager.removeItem(item);
                        return;
                    }
                }
            }
        });
    }

    private Optional<Item> wrapInChestIfNeeded(Loot loot) {
        Optional<ItemMetadata> chest = loot.getInternalItemNames().stream()
                .map(gameManager.getItemStorage()::get)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(ItemMetadata::isChest)
                .findFirst();

        Item newChest = null;

        if (chest.isPresent()) {
            newChest = new ItemBuilder()
                    .from(chest.get())
                    .loot(loot)
                    .create();

            gameManager.getEntityManager().saveItem(newChest);
        }

        return Optional.ofNullable(newChest);
    }
}