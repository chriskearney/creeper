package com.comandante.creeper.items;


import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.use.DefaultApplyEffectsStats;
import com.comandante.creeper.items.use.FireSauceUseAction;
import com.comandante.creeper.items.use.LightningSpellBookUseAction;
import com.comandante.creeper.items.use.StickOfJusticeUseAction;
import com.comandante.creeper.player.Player;
import org.apache.log4j.Logger;

import java.util.Optional;

public class ItemUseHandler {

    private static final Logger log = Logger.getLogger(ItemUseHandler.class);
    private GameManager gameManager;

    public ItemUseHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void handle(Player player, Item item, Optional<UseCommand.UseItemOn> useItemOn) {
        ItemUseAction itemUseAction = null;
        Optional<ItemMetadata> itemMetadataOptional = gameManager.getItemStorage().get(item.getInternalItemName());
        if (!itemMetadataOptional.isPresent()) {
            return;
        }
        ItemMetadata itemMetadata = itemMetadataOptional.get();
        switch (itemMetadata.getInternalItemName()) {
            case "lightning spellbook":
                itemUseAction = new LightningSpellBookUseAction(itemMetadata);
                break;
            case "stick of justice":
                itemUseAction = new StickOfJusticeUseAction(itemMetadata);
                break;
            case "fire sauce":
                itemUseAction = new FireSauceUseAction(itemMetadata);
                break;
            default:
                if ((item.getEffects() != null && item.getEffects().size() > 0) || (item.getItemApplyStats() != null)) {
                    itemUseAction = new DefaultApplyEffectsStats(itemMetadata);
                }
                break;
        }
        if (itemUseAction != null) {
            itemUseAction.executeAction(gameManager, player, item, useItemOn);
            itemUseAction.postExecuteAction(gameManager, player, item);
        }
    }

    public static void incrementUses(Item item) {
        item.setNumberOfUses(item.getNumberOfUses() + 1);
    }
}

