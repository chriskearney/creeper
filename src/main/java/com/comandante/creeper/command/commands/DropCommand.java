package com.comandante.creeper.command.commands;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.PlayerMetadata;
import com.comandante.creeper.model.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

public class DropCommand extends Command {

    private final static String helpDescription = "Remove an item in your inventory and drop it in your current room.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "drop".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;
    private final CreeperSession creeperSession;

    public DropCommand(String playerId, GameManager gameManager, String originalMessage, CreeperSession creeperSession) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
        this.creeperSession = creeperSession;
    }

    @Override
    public void run() {
        ArrayList<String> originalMessageParts = getOriginalMessageParts();
        if (originalMessageParts.size() == 1) {
            commandWrite("No item specified.");
            return;
        }
        originalMessageParts.remove(0);
        String itemTarget = Joiner.on(" ").join(originalMessageParts);
        for (String inventoryId : getGameManager().getPlayerManager().getPlayerMetadata(getPlayerId()).getInventory()) {
            Item itemEntity = getGameManager().getEntityManager().getItemEntity(inventoryId);
            if (itemEntity.getItemTriggers().contains(itemTarget)) {
                itemEntity.setWithPlayer(false);
                Room playerCurrentRoom = getGameManager().getRoomManager().getPlayerCurrentRoom(getGameManager().getPlayerManager().getPlayer(getPlayerId())).get();
                getGameManager().placeItemInRoom(playerCurrentRoom.getRoomId(), itemEntity.getItemId());
                PlayerMetadata playerMetadata = getGameManager().getPlayerManager().getPlayerMetadata(getPlayerId());
                playerMetadata.removeInventoryEntityId(itemEntity.getItemId());
                getGameManager().getPlayerManager().savePlayerMetadata(playerMetadata);
                getGameManager().getItemDecayManager().addItem(itemEntity);
                getGameManager().getEntityManager().addItem(itemEntity);
                roomSay(playerCurrentRoom.getRoomId(),getGameManager().getPlayerManager().getPlayer(getPlayerId()).getPlayerName() + " dropped " + itemEntity.getItemName());
                return;
            }
        }
    }
}
