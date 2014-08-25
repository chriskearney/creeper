package com.comandante.creeper.command.commands;


import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.PlayerMetadata;
import com.google.common.collect.ImmutableList;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.Arrays;

public class InventoryCommand extends Command {

    private final static String helpDescription = "List your inventory.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>()
            .add("inventory".toLowerCase())
            .add("i")
            .build();

    private final static boolean isCaseSensitiveTriggers = false;

    public InventoryCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        PlayerMetadata playerMetadata = getGameManager().getPlayerManager().getPlayerMetadata(getPlayerId());
        ArrayList<String> inventory = new ArrayList<String>(Arrays.asList(playerMetadata.getInventory()));
        Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        StringBuilder sb = new StringBuilder();
        sb.append(new Ansi().fg(Ansi.Color.CYAN).toString());
        sb.append("----Inventory-----\r\n");
        sb.append(new Ansi().reset().toString());
        for (String inventoryId: inventory) {
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
        getGameManager().getChannelUtils().write(getPlayerId(), sb.toString());
    }
}
