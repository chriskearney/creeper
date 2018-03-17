package com.comandante.creeper.items.use;

import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemUseAction;
import com.comandante.creeper.player.Player;
import com.google.common.collect.Sets;

import java.util.Optional;
import java.util.Set;

public class BaseUseAction implements ItemUseAction {
    @Override
    public String getInternalItemName() {
        throw new RuntimeException("Need to implement getInternalItemName");
    }

    @Override
    public void executeAction(GameManager gameManager, Player player, Item item, Optional<UseCommand.UseItemOn> useItemOn) {
        throw new RuntimeException("Need to implement executeAction");
    }

    @Override
    public void postExecuteAction(GameManager gameManager, Player player, Item item) {
        if (item.isDisposable()) {
            if (item.getNumberOfUses() < item.getMaxUses()) {
                gameManager.getEntityManager().saveItem(item);
            } else {
                gameManager.getChannelUtils().write(player.getPlayerId(), item.getItemName() +  " has been consumed.\r\n", true);
                player.removeInventoryId(item.getItemId());
                gameManager.getEntityManager().removeItem(item);
            }
        }
    }

    @Override
    public Set<Effect> getEffects() {
        return Sets.newHashSet();
    }
}
