package com.comandante.creeper.player;

import com.comandante.creeper.npc.Npc;

import static com.comandante.creeper.player.PlayerClass.*;

public interface DamageProcessor {

    long getAttackAmount(Player player, Npc npc);

    int getChanceToHit(Player player, Npc npc);

    int getCriticalChance(Player player, Npc npc);

    public static DamageProcessor getDamageProcessor(PlayerClass playerClass) {
        switch (playerClass) {
            case WARRIOR:
            case WIZARD:
            case RANGER:
            case SHAMAN:
            case BASIC:
                return new BasicPlayerDamageProcessor();
            default:
                return new BasicPlayerDamageProcessor();
        }
    }
}
