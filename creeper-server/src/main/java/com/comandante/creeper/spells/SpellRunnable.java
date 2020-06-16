package com.comandante.creeper.spells;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;

import java.util.Optional;

public interface SpellRunnable {
    void run(Player sourcePlayer, Optional<Npc> destinationNpc, Optional<Player> destinationPlayer, GameManager gameManager);
    String getName();
}
