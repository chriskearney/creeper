package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Item;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class EquipCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("equip");
    final static String description = "Equip an item.";
    final static String correctUsage = "equip <equipment item name>";


    public EquipCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() == 1) {
                write("No equipment item specified.");
                return;
            }
            originalMessageParts.remove(0);
            String itemTarget = Joiner.on(" ").join(originalMessageParts);
            List<Item> inventory = player.getInventory();
            if (inventory != null) {
                for (Item item : inventory) {
                    if (item.getItemTriggers().contains(itemTarget)) {
                        if (item.getEquipment() == null) {
                            write("Item is not equipable.");
                            return;
                        }
                        player.equip(item);
                        return;
                    }
                }
            } else {
                write("Your inventory is empty.");
            }
        });
    }
}