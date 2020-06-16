package com.comandante.creeper.command.commands;

import com.comandante.creeper.items.Item;
import com.comandante.creeper.core_game.GameManager;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class DropCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("drop", "d");
    final static String description = "Drop an item.";
    final static String correctUsage = "drop <item name>";

    public DropCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() == 1) {
                write("No item specified.");
                return;
            }
            originalMessageParts.remove(0);
            String itemTarget = Joiner.on(" ").join(originalMessageParts);
            for (Item item : player.getInventory()) {
                if (item.getItemTriggers().contains(itemTarget)) {
                    player.dropItem(item.getItemId());
                    return;
                }
            }
        });
    }
}
