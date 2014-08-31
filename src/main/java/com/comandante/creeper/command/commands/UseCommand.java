package com.comandante.creeper.command.commands;


import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.Items.ItemUseHandler;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class UseCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("use");
    final static String description = "Use an item.";

    public UseCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession session = getCreeperSession(e.getChannel());
            List<String> originalMessageParts = getOriginalMessageParts(e);
            if (originalMessageParts.size() == 1) {
                getGameManager().getChannelUtils().write(getPlayerId(session), "No item specified.");
                return;
            }
            originalMessageParts.remove(0);
            String itemTarget = Joiner.on(" ").join(originalMessageParts);
            for (String inventoryId : getGameManager().getPlayerManager().getPlayerMetadata(getPlayerId(session)).getInventory()) {
                Item itemEntity = getGameManager().getEntityManager().getItemEntity(inventoryId);
                if (itemEntity.getItemTriggers().contains(itemTarget)) {
                    new ItemUseHandler(itemEntity, session, getGameManager(), getPlayerId(session)).handle();
                    return;
                }
            }
            new ItemUseHandler(ItemType.UNKNOWN.create(), session, getGameManager(), getPlayerId(session)).handle();
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
