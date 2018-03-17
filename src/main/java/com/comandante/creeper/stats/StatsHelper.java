package com.comandante.creeper.stats;

public class StatsHelper {

    public static Stats getDifference(Stats modifiedStats, Stats origStats) {
        StatsBuilder statsBuilder = new StatsBuilder();
        statsBuilder.setAgile(modifiedStats.getAgile() - origStats.getAgile());
        statsBuilder.setIntelligence(modifiedStats.getIntelligence() - origStats.getIntelligence());
        statsBuilder.setAim(modifiedStats.getAim() - origStats.getAim());
        statsBuilder.setArmorRating(modifiedStats.getArmorRating() - origStats.getArmorRating());
        statsBuilder.setCurrentHealth(modifiedStats.getCurrentHealth() - origStats.getCurrentHealth());
        statsBuilder.setMaxHealth(modifiedStats.getMaxHealth() - origStats.getMaxHealth());
        statsBuilder.setExperience(modifiedStats.getExperience() - origStats.getExperience());
        statsBuilder.setMeleSkill(modifiedStats.getMeleSkill() - origStats.getMeleSkill());
        statsBuilder.setNumberOfWeaponRolls(modifiedStats.getNumberOfWeaponRolls() - origStats.getNumberOfWeaponRolls());
        statsBuilder.setStrength(modifiedStats.getStrength() - origStats.getStrength());
        statsBuilder.setWeaponRatingMax(modifiedStats.getWeaponRatingMax() - origStats.getWeaponRatingMax());
        statsBuilder.setWeaponRatingMin(modifiedStats.getWeaponRatingMin() - origStats.getWeaponRatingMin());
        statsBuilder.setWillpower(modifiedStats.getWillpower() - origStats.getWillpower());
        statsBuilder.setCurrentMana(modifiedStats.getCurrentMana() - origStats.getCurrentMana());
        statsBuilder.setMaxMana(modifiedStats.getMaxMana() - origStats.getMaxMana());
        statsBuilder.setForaging(modifiedStats.getForaging() - origStats.getForaging());
        statsBuilder.setInventorySize(modifiedStats.getInventorySize() - origStats.getInventorySize());
        statsBuilder.setMaxEffects(modifiedStats.getMaxEffects() - origStats.getMaxEffects());
        statsBuilder.setLockPicking(modifiedStats.getLockPicking() - origStats.getLockPicking());
        return statsBuilder.createStats();
    }

    public static void combineStats(Stats orig, Stats combine) {
        orig.setAgile(orig.getAgile() + combine.getAgile());
        orig.setIntelligence(orig.getIntelligence() + combine.getIntelligence());
        orig.setAim(orig.getAim() + combine.getAim());
        orig.setArmorRating(orig.getArmorRating() + combine.getArmorRating());
        orig.setCurrentHealth(orig.getCurrentHealth() + combine.getCurrentHealth());
        orig.setMaxHealth(orig.getMaxHealth() + combine.getMaxHealth());
        orig.setExperience(orig.getExperience() + combine.getExperience());
        orig.setMeleSkill(orig.getMeleSkill() + combine.getMeleSkill());
        orig.setNumberOfWeaponRolls(orig.getNumberOfWeaponRolls() + combine.getNumberOfWeaponRolls());
        orig.setStrength(orig.getStrength() + combine.getStrength());
        orig.setWeaponRatingMax(orig.getWeaponRatingMax() + combine.getWeaponRatingMax());
        orig.setWeaponRatingMin(orig.getWeaponRatingMin() + combine.getWeaponRatingMin());
        orig.setWillpower(orig.getWillpower() + combine.getWillpower());
        orig.setCurrentMana(orig.getCurrentMana() + combine.getCurrentMana());
        orig.setMaxMana(orig.getMaxMana() + combine.getMaxMana());
        orig.setForaging(orig.getForaging() + combine.getForaging());
        orig.setInventorySize(orig.getInventorySize() + combine.getInventorySize());
        orig.setMaxEffects(orig.getMaxEffects() + combine.getMaxEffects());
        orig.setLockPicking(orig.getLockPicking() - combine.getLockPicking());
    }

    public static void inverseStats(Stats stats) {
        stats.setAgile(-stats.getAgile());
        stats.setAim(-stats.getAim());
        stats.setIntelligence(-stats.getIntelligence());
        stats.setArmorRating(-stats.getArmorRating());
        stats.setCurrentHealth(-stats.getCurrentHealth());
        stats.setMaxHealth(-stats.getMaxHealth());
        stats.setExperience(-stats.getExperience());
        stats.setMeleSkill(-stats.getMeleSkill());
        stats.setNumberOfWeaponRolls(-stats.getNumberOfWeaponRolls());
        stats.setStrength(-stats.getStrength());
        stats.setWeaponRatingMax(-stats.getWeaponRatingMax());
        stats.setWeaponRatingMin(-stats.getWeaponRatingMin());
        stats.setWillpower(-stats.getWillpower());
        stats.setCurrentMana(-stats.getCurrentMana());
        stats.setMaxMana(-stats.getMaxMana());
        stats.setForaging(-stats.getForaging());
        stats.setInventorySize(-stats.getInventorySize());
        stats.setMaxEffects(-stats.getMaxEffects());
        stats.setLockPicking(-stats.getLockPicking());
    }

}
