package com.comandante.creeper.items.use;

import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.items.ItemUseAction;
import com.comandante.creeper.player.CoolDown;
import com.comandante.creeper.player.CoolDownType;
import com.comandante.creeper.player.Player;

import java.util.Set;

public class FireSauceUseAction implements ItemUseAction {

    private final ItemMetadata itemMetadata;

    public FireSauceUseAction(ItemMetadata itemMetadata) {
        this.itemMetadata = itemMetadata;
    }

    @Override
    public String getInternalItemName() {
        return itemMetadata.getInternalItemName();
    }

    @Override
    public void executeAction(GameManager gameManager, Player player, Item item, UseCommand.UseItemOn useItemOn) {
        gameManager.writeToPlayerCurrentRoom(player.getPlayerId(), player.getPlayerName() + " drinks a " + itemMetadata.getItemName() + ".");
        player.addCoolDown(new CoolDown(CoolDownType.FIRE_SAUCE));
    }

    @Override
    public void postExecuteAction(GameManager gameManager, Player player, Item item) {
        player.removeInventoryId(item.getItemId());
        gameManager.getEntityManager().removeItem(item);
    }

    @Override
    public Set<Effect> getEffects() {
        return null;
    }
}
