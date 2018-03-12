package com.comandante.creeper.items.use;

import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.items.ItemUseAction;
import com.comandante.creeper.player.Player;

import java.util.Optional;
import java.util.Set;

public class StickOfJusticeUseAction implements ItemUseAction {

    private final ItemMetadata itemMetadata;

    public StickOfJusticeUseAction(ItemMetadata itemMetadata) {
        this.itemMetadata = itemMetadata;
    }

    @Override
    public String getInternalItemName() {
        return itemMetadata.getInternalItemName();
    }


    @Override
    public void executeAction(GameManager gameManager, Player player, Item item, Optional<UseCommand.UseItemOn> useItemOn) {

        if (!useItemOn.isPresent()) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "Stick Of Justice must be used on something or someone.");
            return;
        }

        UseCommand.UseItemOn on = useItemOn.get();

        if (!on.getTarget().isPresent()) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "You must use the Stick Of Justice on someone who deserves it.");
            return;
        }

        Optional<Player> playerByCommandTarget = gameManager.getPlayerManager().getPlayerByCommandTarget(player.getCurrentRoom(), on.getTarget().get());
        if (!playerByCommandTarget.isPresent()) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "You must use the Stick Of Justice on someone who deserves it.");
            return;
        }

        Player targetedPlayer = playerByCommandTarget.get();

        gameManager.detainPlayer(targetedPlayer);
    }

    @Override
    public void postExecuteAction(GameManager gameManager, Player player, Item item) {
    }

    @Override
    public Set<Effect> getEffects() {
        return null;
    }
}
