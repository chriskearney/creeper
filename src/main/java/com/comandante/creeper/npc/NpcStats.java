package com.comandante.creeper.npc;

import com.comandante.creeper.stat.StatsBuilder;

public class NpcStats {
    public final static StatsBuilder JOE_NPC = new StatsBuilder()
            .setStrength(5)
            .setWillpower(1)
            .setAim(1)
            .setAgile(1)
            .setArmorRating(5)
            .setMeleSkill(5)
            .setHealth(100)
            .setWeaponRatingMin(5)
            .setWeaponRatingMax(10)
            .setNumberweaponOfRolls(1);
}
