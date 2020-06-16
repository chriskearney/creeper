package com.comandante.creeper.npc;

import com.comandante.creeper.player.DamageProcessor;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;

import java.util.Random;


public class BasicNpcPlayerDamageProcessor implements DamageProcessor {

    private final Random random = new Random();

    @Override
    public long getAttackAmount(Player player, Npc npc) {
        Stats playerStats = player.getPlayerStatsWithEquipmentAndLevel();
        Stats npcStats = npc.getStats();
        long rolls = 0;
        long totDamage = 0;
        while (rolls <= npcStats.getNumberOfWeaponRolls()) {
            rolls++;
            totDamage = totDamage + randInt((int) npcStats.getWeaponRatingMin(), (int) npcStats.getWeaponRatingMax());
        }
        long i = npcStats.getStrength() + totDamage - playerStats.getArmorRating();
        if (i < 0) {
            return 0;
        } else {
            return i;
        }
    }

    @Override
    public int getChanceToHit(Player player, Npc npc) {
        Stats playerStats = player.getPlayerStatsWithEquipmentAndLevel();
        Stats npcStats = npc.getStats();
        return (int) ((npcStats.getStrength() + npcStats.getMeleSkill()) * 5 - playerStats.getAgile() * 5);
    }

    @Override
    public int getCriticalChance(Player player, Npc npc) {
        //y =.20({x}) + 0
        return (int) (5 + (.20f * npc.getStats().getAim()));
    }

    private int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
