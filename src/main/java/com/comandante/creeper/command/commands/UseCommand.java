package com.comandante.creeper.command.commands;


import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.Items.ItemUseHandler;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

public class UseCommand extends Command {

    private final static String helpDescription = "Use an item in your inventory.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "use".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;
    private final CreeperSession creeperSession;

    public UseCommand(String playerId, GameManager gameManager, String originalMessage, CreeperSession creeperSession) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
        this.creeperSession = creeperSession;
    }

    @Override
    public void run() {
        ArrayList<String> originalMessageParts = getOriginalMessageParts();
        if (originalMessageParts.size() == 1) {
            getGameManager().getChannelUtils().write(getPlayerId(), "No item specified.");
            return;
        }
        String itemTarget = originalMessageParts.get(1);
        for (String inventoryId : getGameManager().getPlayerManager().getPlayerMetadata(getPlayerId()).getInventory()) {
            Item itemEntity = getGameManager().getEntityManager().getItemEntity(inventoryId);
            if (itemEntity.getShortName().equals(itemTarget)) {
                new ItemUseHandler(itemEntity, creeperSession, getGameManager(), getPlayerId()).handle();
                return;
            }
        }
        new ItemUseHandler(ItemType.UNKNOWN.create(), creeperSession, getGameManager(), getPlayerId()).handle();
    }
}
