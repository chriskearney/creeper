package com.comandante.creeper.items;

import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;

import java.util.Optional;
import java.util.Set;

public interface ItemUseAction {
    String getInternalItemName();

    void executeAction(GameManager gameManager, Player player, Item item, Optional<UseCommand.UseItemOn> useItemOn);

    void postExecuteAction(GameManager gameManager, Player player, Item item);

    Set<Effect> getEffects();

}
