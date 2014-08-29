package com.comandante.creeper.player;


import com.comandante.creeper.stat.StatsBuilder;

public class PlayerStats {

    public final static StatsBuilder DEFAULT_PLAYER = new StatsBuilder()
            .setStrength(10)
            .setWillpower(1)
            .setAim(1)
            .setAgile(1)
            .setArmorRating(2)
            .setMeleSkill(10)
            .setHealth(100)
            .setWeaponRatingMin(10)
            .setWeaponRatingMax(20)
            .setNumberweaponOfRolls(1);
}
