package com.comandante.creeper.command.commands;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Item;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class UseCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("use");
    final static String description = "Use an item.";
    final static String correctUsage = "use <item name>";

    public UseCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {

            UseItemOn useItemOn = new UseItemOn(originalMessageParts);

            if (Strings.isNullOrEmpty(useItemOn.getItem())) {
                write("No item specified.");
                return;
            }

            Optional<Item> inventoryItemOptional = player.getInventoryItem(useItemOn.getItem());
            if (!inventoryItemOptional.isPresent()) {
                write("Useable item is not found in your inventory.\r\n");
                return;
            }

            gameManager.getItemUseHandler().handle(player, inventoryItemOptional.get(), Optional.of(useItemOn));
        });
    }

    public static class UseItemOn {

        public static String ON_KEYWORD = "on";

        private final Optional<String> target;
        private final String item;

        public UseItemOn(List<String> originalMessageParts) {
            originalMessageParts.remove(0);
            String fullCommand = Joiner.on(" ").join(originalMessageParts);
            item = getItem(fullCommand);
            target = getItemTarget(fullCommand);
        }

        private static String getItem(String fullCommand) {
            if (!StringUtils.containsIgnoreCase(fullCommand, ON_KEYWORD)) {
                return fullCommand;
            }
            List<String> parts = Arrays.asList(fullCommand.split("(?i)" + ON_KEYWORD, 2));
            String item = parts.get(0);
            return item.trim();
        }

        private static Optional<String> getItemTarget(String fullCommand) {
            if (!StringUtils.containsIgnoreCase(fullCommand, ON_KEYWORD)) {
                return Optional.empty();
            }
            List<String> parts = Arrays.asList(fullCommand.split("(?i)" + ON_KEYWORD, 2));
            if (parts.size() > 1) {
                String item = parts.get(1);
                return Optional.of(item.trim());
            }
            return Optional.empty();
        }

        public Optional<String> getTarget() {
            return target;
        }

        public String getItem() {
            return item;
        }
    }
}
