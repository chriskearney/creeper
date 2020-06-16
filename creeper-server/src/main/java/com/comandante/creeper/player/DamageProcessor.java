package com.comandante.creeper.player;

import com.comandante.creeper.npc.Npc;

public interface DamageProcessor {

    long getAttackAmount(Player player, Npc npc);

    int getChanceToHit(Player player, Npc npc);

    int getCriticalChance(Player player, Npc npc);
}
