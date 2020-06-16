package com.comandante.creeper.stats;


public class DefaultStats {

    public final static StatsBuilder DEFAULT_PLAYER = new StatsBuilder()
            .setStrength(9)
            .setIntelligence(9)
            .setWillpower(9)
            .setAim(9)
            .setAgile(9)
            .setArmorRating(4)
            .setMeleSkill(9)
            .setCurrentHealth(100)
            .setMaxHealth(100)
            .setWeaponRatingMin(4)
            .setWeaponRatingMax(6)
            .setNumberOfWeaponRolls(1)
            .setExperience(0)
            .setCurrentMana(100)
            .setMaxMana(100)
            .setForaging(0)
            .setInventorySize(10)
            .setMaxEffects(4);
}
