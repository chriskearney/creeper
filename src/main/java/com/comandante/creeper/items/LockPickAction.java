package com.comandante.creeper.items;

import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;

import java.util.Optional;
import java.util.Set;

public class LockPickAction implements ItemUseAction {

    private final ItemMetadata itemMetadata;

    public LockPickAction(ItemMetadata itemMetadata) {
        this.itemMetadata = itemMetadata;
    }

    @Override
    public String getInternalItemName() {
        return itemMetadata.getInternalItemName();
    }

    @Override
    public void executeAction(GameManager gameManager, Player player, Item item, Optional<UseCommand.UseItemOn> useItemOn) {

        if (!useItemOn.isPresent()) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "What do you want to use the lock pick on?");
            return;
        }

        UseCommand.UseItemOn on = useItemOn.get();

        if (!on.getTarget().isPresent()) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "What do you want to use the lock pick on?");
            return;
        }

        Optional<Item> inventoryItem = player.getInventoryItem(on.getTarget().get());
        if (!inventoryItem.isPresent()) {
            return;
        }

        if (!inventoryItem.get().isChest()) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "A lock pick must be used on a chest.");
        }




    }

    @Override
    public void postExecuteAction(GameManager gameManager, Player player, Item item) {

    }

    @Override
    public Set<Effect> getEffects() {
        return null;
    }
}
