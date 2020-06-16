package com.comandante.creeper.spells;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;

import java.util.Optional;

public class Spells {

    private GameManager gameManager;

    private static final Logger log = Logger.getLogger(Spells.class);

    public Spells(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void executeSpell(Player sourcePlayer, Optional<Npc> destinationNpc, Optional<Player> destinationPlayer, SpellRunnable spellRunnable) {
        try {
            spellRunnable.run(sourcePlayer, destinationNpc, destinationPlayer, gameManager);
        } catch (Exception e) {
            log.error("Problem executing spell.", e);
        }
    }

    public Optional<SpellRunnable> getSpellRunnable(String triggerName) {
        if (Sets.newHashSet("lightning", "l").contains(triggerName)) {
            return Optional.of(new LightningSpell(gameManager));
        }
        return Optional.empty();
    }
}


