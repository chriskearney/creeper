package com.comandante.creeper.player;

import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.stats.Stats;

import java.util.Random;

public class BasicPlayerDamageProcessor implements DamageProcessor {

    private final Random random = new Random();

    @Override
    public long getAttackAmount(Player player, Npc npc) {
        Stats playerStats = player.getPlayerStatsWithEquipmentAndLevel();
        Stats npcStats = npc.getStats();
        long rolls = 0;
        long totDamage = 0;
        while (rolls <= playerStats.getNumberOfWeaponRolls()) {
            rolls++;
            totDamage = totDamage + randInt((int) playerStats.getWeaponRatingMin(), (int) playerStats.getWeaponRatingMax());
        }
        long i = playerStats.getStrength() + totDamage - npcStats.getArmorRating();
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
        return (int) ((playerStats.getStrength() + playerStats.getMeleSkill()) * 5 - npcStats.getAgile() * 5);
    }

    @Override
    public int getCriticalChance(Player player, Npc npc) {
        //y =.20({x}) + 0
        return (int) (5 + (.20f * player.getPlayerStatsWithEquipmentAndLevel().getAim()));
    }

    @Override
    public long getAttackAmount(Player player, Player targetPlayer) {
        Stats playerStats = player.getPlayerStatsWithEquipmentAndLevel();
        Stats targetPlayerStats = targetPlayer.getPlayerStatsWithEquipmentAndLevel();
        long rolls = 0;
        long totDamage = 0;
        while (rolls <= playerStats.getNumberOfWeaponRolls()) {
            rolls++;
            totDamage = totDamage + randInt((int) playerStats.getWeaponRatingMin(), (int) playerStats.getWeaponRatingMax());
        }
        long i = playerStats.getStrength() + totDamage - targetPlayerStats.getArmorRating();
        if (i < 0) {
            return 0;
        } else {
            return i;
        }
    }

    @Override
    public int getChanceToHit(Player player, Player targetPlayer) {
        Stats playerStats = player.getPlayerStatsWithEquipmentAndLevel();
        Stats targetPlayerStats = targetPlayer.getPlayerStatsWithEquipmentAndLevel();
        return (int) ((playerStats.getStrength() + playerStats.getMeleSkill()) * 5 - targetPlayerStats.getAgile() * 5);
    }

    @Override
    public int getCriticalChance(Player player, Player targetPlayer) {
        //y =.20({x}) + 0
        return (int) (5 + (.20f * player.getPlayerStatsWithEquipmentAndLevel().getAim()));
    }

    private int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}
