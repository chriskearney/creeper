package com.comandante.creeper.server.command;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.room.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class DropCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("d", "drop");
    final static String description = "Drop an item";

    public DropCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            GameManager gameManager = getGameManager();
            CreeperSession session = getCreeperSession(e.getChannel());
            final String playerId = getPlayerId(session);
            List<String> origMessageParts = getOriginalMessageParts(e);
            if (origMessageParts.size() == 1) {
                gameManager.getChannelUtils().write(playerId, "No item specified.");
                return;
            }
            origMessageParts.remove(0);
            String itemTarget = Joiner.on(" ").join(origMessageParts);
            for (String inventoryId : gameManager.getPlayerManager().getPlayerMetadata(playerId).getInventory()) {
                Item itemEntity = gameManager.getEntityManager().getItemEntity(inventoryId);
                if (itemEntity.getItemTriggers().contains(itemTarget)) {
                    itemEntity.setWithPlayer(false);
                    Room playerCurrentRoom = gameManager.getRoomManager().getPlayerCurrentRoom(gameManager.getPlayerManager().getPlayer(playerId)).get();
                    gameManager.placeItemInRoom(playerCurrentRoom.getRoomId(), itemEntity.getItemId());
                    PlayerMetadata playerMetadata = gameManager.getPlayerManager().getPlayerMetadata(playerId);
                    playerMetadata.removeInventoryEntityId(itemEntity.getItemId());
                    gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
                    gameManager.getItemDecayManager().addItem(itemEntity);
                    gameManager.getEntityManager().addItem(itemEntity);
                    gameManager.roomSay(playerCurrentRoom.getRoomId(), gameManager.getPlayerManager().getPlayer(playerId).getPlayerName() + " dropped " + itemEntity.getItemName(), playerId);
                    return;
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
