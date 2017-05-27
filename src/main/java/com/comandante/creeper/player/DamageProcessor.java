package com.comandante.creeper.player;

import com.comandante.creeper.npc.Npc;

public interface DamageProcessor {

    long getAttackAmount(Player player, Npc npc);

    int getChanceToHit(Player player, Npc npc);

    int getCriticalChance(Player player, Npc npc);

    long getAttackAmount(Player player, Player targetPlayer);

    int getChanceToHit(Player player, Player targetPlayer);

    int getCriticalChance(Player player, Player targetPlayer);
}
