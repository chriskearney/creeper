package com.comandante.creeper.merchant;

import com.comandante.creeper.command.commands.CommandAuditLog;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.server.model.CreeperSession;
import com.comandante.creeper.server.player_communication.Color;
import com.google.common.collect.Maps;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.text.NumberFormat;
import java.util.*;

public class MerchantCommandHandler extends SimpleChannelUpstreamHandler {

    private final GameManager gameManager;
    private final Merchant merchant;
    private final MerchantManager merchantManager;

    public MerchantCommandHandler(GameManager gameManager, Merchant merchant) {
        this.gameManager = gameManager;
        this.merchant = merchant;
        this.merchantManager = new MerchantManager(gameManager);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        CreeperSession creeperSession = (CreeperSession) e.getChannel().getAttachment();
        Player playerByUsername = gameManager.getPlayerManager().getPlayerByUsername(creeperSession.getUsername().get());
        Map<Integer, InventoryItemForSale> inventoryMenu = getInventoryMenu(playerByUsername);
        try {
            String message = (String) e.getMessage();
            CommandAuditLog.logCommand(message, playerByUsername.getPlayerName());
            String cmd = message.replaceFirst("\\s+$", "");
            String[] split = null;
            if (cmd.contains(" ")) {
                split = message.split(" ");
                cmd = split[0];
            }
            if (cmd.equalsIgnoreCase("buy")) {
                if (split != null && isInteger(split[1])) {
                    Integer desiredItem = Integer.parseInt(split[1]);
                    merchantManager.purchaseItem(merchant, desiredItem, playerByUsername);
                } else {
                    gameManager.getChannelUtils().write(playerByUsername.getPlayerId(), merchant.getMenu() + "\r\n");
                }
            } else if (cmd.equalsIgnoreCase("sell")) {
                if (split != null && isInteger(split[1])) {
                    Integer desiredItem = Integer.parseInt(split[1]);
                    if (inventoryMenu.containsKey(desiredItem)) {
                        InventoryItemForSale inventoryItemForSale = inventoryMenu.get(desiredItem);
                        Item item = inventoryItemForSale.getItem();
                        playerByUsername.incrementGold(inventoryItemForSale.getCost());
                        gameManager.getChannelUtils().write(playerByUsername.getPlayerId(), "You have received: " + inventoryItemForSale.getCost() + Color.YELLOW + " gold" + Color.RESET + " for " + item.getItemName() + "\r\n");
                        playerByUsername.removeInventoryId(item.getItemId());
                        gameManager.getEntityManager().removeItem(item);
                        printInvMenu(playerByUsername, getInventoryMenu(playerByUsername));
                    }
                } else {
                    printInvMenu(playerByUsername, inventoryMenu);
                }
            } else if (cmd.equalsIgnoreCase("done")) {
                gameManager.getChannelUtils().write(playerByUsername.getPlayerId(), "Thanks, COME AGAIN." + "\r\n" + "\r\n" + "\r\n", true);
                e.getChannel().getPipeline().addLast(UUID.randomUUID().toString(), creeperSession.getGrabMerchant().get().getValue());
                return;
            }
            gameManager.getChannelUtils().write(playerByUsername.getPlayerId(), "\r\n" + buildPrompt());

        } finally {
            e.getChannel().getPipeline().remove(ctx.getHandler());
            super.messageReceived(ctx, e);
        }
    }

    private void printInvMenu(Player player, Map<Integer, InventoryItemForSale> inventoryMenu ) {
        if (inventoryMenu.size() == 0) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "You have nothing in your inventory of value." + "\r\n");
        } else {
            gameManager.getChannelUtils().write(player.getPlayerId(), getInventoryPrintMenu(inventoryMenu));
        }
    }

    public Map<Integer, InventoryItemForSale> getInventoryMenu(Player player) {
        Map<Integer, InventoryItemForSale> inventoryItemsForSale = Maps.newHashMap();
        List<Item> inventory = player.getInventory();
        int inv = 1;
        for (Item itemEntity : inventory) {
            int valueInGold = itemEntity.getValueInGold();
            if (valueInGold == 0) {
                valueInGold = itemEntity.getValueInGold();
            }
            if (valueInGold > 0) {
                inventoryItemsForSale.put(inv, new InventoryItemForSale(valueInGold, itemEntity));
                inv++;
            }
        }
        return inventoryItemsForSale;
    }

    public String getInventoryPrintMenu(Map<Integer, InventoryItemForSale> menu) {
        Table t = new Table(3, BorderStyle.CLASSIC_COMPATIBLE,
                ShownBorders.HEADER_FIRST_AND_LAST_COLLUMN);
        t.setColumnWidth(0, 5, 5);
        t.setColumnWidth(1, 12, 12);
        t.setColumnWidth(2, 50, 69);
        t.addCell("#");
        t.addCell("price");
        t.addCell("description");
        Iterator<Map.Entry<Integer, InventoryItemForSale>> entries = menu.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, InventoryItemForSale> next = entries.next();
            t.addCell(String.valueOf(next.getKey()));
            t.addCell(NumberFormat.getNumberInstance(Locale.US).format(next.getValue().getCost()));
            t.addCell(next.getValue().getItem().getItemDescription());
        }
        return t.render() + "\r\n";
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    class InventoryItemForSale {
        private final Item item;
        private final Integer cost;

        public InventoryItemForSale(Integer cost, Item item) {
            this.cost = cost;
            this.item = item;
        }

        public Integer getCost() {
            return cost;
        }

        public Item getItem() {
            return item;
        }
    }

    public static String buildPrompt() {
        return "[" + Color.GREEN + "MERCHANT" + Color.RESET + " - BUY | SELL | DONE] ";
    }
}
