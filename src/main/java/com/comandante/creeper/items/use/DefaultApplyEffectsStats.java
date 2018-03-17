package com.comandante.creeper.items.use;

import com.comandante.creeper.command.commands.UseCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.items.ItemUseAction;
import com.comandante.creeper.items.ItemUseHandler;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.Set;

public class DefaultApplyEffectsStats extends BaseUseAction {

    private final String internalItemName;
    private final Set<Effect> effectSet;
    private final Stats itemApplyStats;
    private static final Logger log = Logger.getLogger(DefaultApplyEffectsStats.class);

    public DefaultApplyEffectsStats(ItemMetadata itemMetadata) {
        this.internalItemName = itemMetadata.getInternalItemName();
        this.effectSet = itemMetadata.getEffects();
        this.itemApplyStats = itemMetadata.getItemApplyStats();
    }

    @Override
    public String getInternalItemName() {
        return internalItemName;
    }

    @Override
    public void executeAction(GameManager gameManager, Player player, Item item, Optional<UseCommand.UseItemOn> useItemOn) {
        String playerName = player.getPlayerName();

        gameManager.writeToPlayerCurrentRoom(player.getPlayerId(), playerName + " uses " + item.getItemName() + ".\r\n");
        if (itemApplyStats.getCurrentMana() > 0) {
            gameManager.getChannelUtils().write(player.getPlayerId(), itemApplyStats.getCurrentMana() + " mana is restored." + "\r\n");
        }
        if (itemApplyStats.getCurrentHealth() > 0) {
            gameManager.getChannelUtils().write(player.getPlayerId(), itemApplyStats.getCurrentHealth() + " health is restored." + "\r\n");
        }
        player.addMana(itemApplyStats.getCurrentMana());
        player.updatePlayerHealth(itemApplyStats.getCurrentHealth(), null);

        processEffects(gameManager, player, effectSet);
    }

    @Override
    public Set<Effect> getEffects() {
        return effectSet;
    }

    public static void processEffects(GameManager gameManager, Player player, Set<Effect> effects) {
        if (effects == null) {
            return;
        }
        for (Effect effect : effects) {
            Effect nEffect = new Effect(effect);
            nEffect.setPlayerId(player.getPlayerId());
            boolean effectResult = player.addEffect(nEffect);
            if (effect.getDurationStats() != null) {
                if (effect.getDurationStats().getCurrentHealth() < 0) {
                    log.error("ERROR! Someone added an effect with a health modifier which won't work for various reasons.");
                    continue;
                }
            }
            if (effectResult) {
                gameManager.getChannelUtils().write(player.getPlayerId(), "You feel " + effect.getEffectName() + "\r\n");
            } else {
                gameManager.getChannelUtils().write(player.getPlayerId(), "Unable to apply effect.\r\n");
            }
        }
    }
}
