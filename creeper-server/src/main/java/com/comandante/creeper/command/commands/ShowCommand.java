package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Item;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class ShowCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("show");
    final static String description = "Show an item in your inventory.";
    final static String correctUsage = "show <item_name>";

    public ShowCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() <= 1) {
                return;
            }
            originalMessageParts.remove(0);
            String target = Joiner.on(" ").join(originalMessageParts);
            for (Item next : player.getInventory()) {
                for (String s : next.getItemTriggers()) {
                    if (s.equalsIgnoreCase(target)) {
                        player.show(next.getItemId());
                        return;
                    }
                }
            }
        });
    }
}