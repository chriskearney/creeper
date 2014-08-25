package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.Items.Item;
import com.comandante.creeper.model.Player;
import com.comandante.creeper.model.Room;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Set;

public class PickUpCommand extends Command {

    private final static String helpDescription = "Pickup an Item.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>()
            .add("pickup".toLowerCase())
            .add("p")
            .build();

    private final static boolean isCaseSensitiveTriggers = false;

    public PickUpCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        ArrayList<String> originalMessageParts = getOriginalMessageParts();
        Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        Room playerCurrentRoom = getGameManager().getRoomManager().getPlayerCurrentRoom(player).get();
        Set<String> itemIds = playerCurrentRoom.getItemIds();
        for (String next : itemIds) {
            Item itemEntity = getGameManager().getEntityManager().getItemEntity(next);
            if (itemEntity.getShortName().equalsIgnoreCase(originalMessageParts.get(1))) {
                getGameManager().acquireItem(player, itemEntity.getItemId());
                roomSay(playerCurrentRoom.getRoomId(), getGameManager().getPlayerManager().getPlayer(getPlayerId()).getPlayerName() + " picked up " + itemEntity.getItemName());
                return;
            }
        }
    }
}
