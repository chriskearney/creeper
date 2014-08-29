package com.comandante.creeper.npc;

import com.comandante.creeper.stat.StatsBuilder;

public class NpcStats {
    public final static StatsBuilder DERPER = new StatsBuilder()
            .setStrength(5)
            .setWillpower(1)
            .setAim(1)
            .setAgile(1)
            .setArmorRating(5)
            .setMeleSkill(5)
            .setCurrentHealth(100)
            .setMaxHealth(100)
            .setWeaponRatingMin(5)
            .setWeaponRatingMax(10)
            .setNumberweaponOfRolls(1);

    public final static StatsBuilder DRUGGED_PIMP = new StatsBuilder()
            .setStrength(5)
            .setWillpower(1)
            .setAim(1)
            .setAgile(1)
            .setArmorRating(5)
            .setMeleSkill(5)
            .setCurrentHealth(150)
            .setMaxHealth(150)
            .setWeaponRatingMin(5)
            .setWeaponRatingMax(10)
            .setNumberweaponOfRolls(1);

}
