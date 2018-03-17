package com.comandante.creeper.stats;

import java.io.Serializable;

import static java.lang.StrictMath.sqrt;

public class Stats implements Serializable {
    private long strength;
    private long intelligence;
    private long willpower;
    private long aim;
    private long agile;
    private long armorRating;
    private long meleSkill;
    private long currentHealth;
    private long maxHealth;
    private long weaponRatingMax;
    private long weaponRatingMin;
    private long numberOfWeaponRolls;
    private long experience;
    private long currentMana;
    private long maxMana;
    private long foraging;
    private long inventorySize;
    private long maxEffects;
    private long lockPicking;

    public Stats(Stats stats) {
        this.strength = stats.strength;
        this.intelligence = stats.intelligence;
        this.willpower = stats.willpower;
        this.aim = stats.aim;
        this.agile = stats.agile;
        this.armorRating = stats.armorRating;
        this.meleSkill = stats.meleSkill;
        this.currentHealth = stats.currentHealth;
        this.maxHealth = stats.maxHealth;
        this.currentHealth = stats.currentHealth;
        this.maxHealth = stats.maxHealth;
        this.weaponRatingMax = stats.weaponRatingMax;
        this.weaponRatingMin = stats.weaponRatingMin;
        this.numberOfWeaponRolls = stats.numberOfWeaponRolls;
        this.experience = stats.experience;
        this.currentMana = stats.currentMana;
        this.foraging = stats.foraging;
        this.maxMana = stats.maxMana;
        this.inventorySize = stats.inventorySize;
        this.maxEffects = stats.maxEffects;
        this.lockPicking = stats.lockPicking;
    }

    public Stats(long strength,
                 long intelligence,
                 long willpower,
                 long aim,
                 long agile,
                 long armorRating,
                 long meleSkill,
                 long currentHealth,
                 long maxHealth,
                 long weaponRatingMax,
                 long weaponRatingMin,
                 long numberOfWeaponRolls,
                 long experience,
                 long currentMana,
                 long maxMana,
                 long foraging,
                 long inventorySize,
                 long maxEffects,
                 long lockPicking) {
        this.strength = strength;
        this.intelligence = intelligence;
        this.willpower = willpower;
        this.aim = aim;
        this.agile = agile;
        this.armorRating = armorRating;
        this.meleSkill = meleSkill;
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
        this.weaponRatingMax = weaponRatingMax;
        this.weaponRatingMin = weaponRatingMin;
        this.numberOfWeaponRolls = numberOfWeaponRolls;
        this.experience = experience;
        this.currentMana = currentMana;
        this.maxMana = maxMana;
        this.foraging = foraging;
        this.inventorySize = inventorySize;
        this.maxEffects = maxEffects;
        this.lockPicking = lockPicking;
    }


    public long getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(long intelligence) {
        this.intelligence = intelligence;
    }

    public long getMaxEffects() {
        return maxEffects;
    }

    public void setMaxEffects(long maxEffects) {
        this.maxEffects = maxEffects;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public long getStrength() {
        return strength;
    }

    public void setStrength(long strength) {
        this.strength = strength;
    }

    public long getWillpower() {
        return willpower;
    }

    public void setWillpower(long willpower) {
        this.willpower = willpower;
    }

    public long getAim() {
        return aim;
    }

    public void setAim(long aim) {
        this.aim = aim;
    }

    public long getAgile() {
        return agile;
    }

    public void setAgile(long agile) {
        this.agile = agile;
    }

    public long getArmorRating() {
        return armorRating;
    }

    public void setArmorRating(long armorRating) {
        this.armorRating = armorRating;
    }

    public long getMeleSkill() {
        return meleSkill;
    }

    public void setMeleSkill(long meleSkill) {
        this.meleSkill = meleSkill;
    }

    public long getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(long currentHealth) {
        this.currentHealth = currentHealth;
    }

    public long getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(long maxHealth) {
        this.maxHealth = maxHealth;
    }

    public long getWeaponRatingMax() {
        return weaponRatingMax;
    }

    public void setWeaponRatingMax(long weaponRatingMax) {
        this.weaponRatingMax = weaponRatingMax;
    }

    public long getWeaponRatingMin() {
        return weaponRatingMin;
    }

    public void setWeaponRatingMin(long weaponRatingMin) {
        this.weaponRatingMin = weaponRatingMin;
    }

    public long getNumberOfWeaponRolls() {
        return numberOfWeaponRolls;
    }

    public void setNumberOfWeaponRolls(long numberOfWeaponRolls) {
        this.numberOfWeaponRolls = numberOfWeaponRolls;
    }

    public long getCurrentMana() {
        return currentMana;
    }

    public long getMaxMana() {
        return maxMana;
    }

    public void setCurrentMana(long currentMana) {
        this.currentMana = currentMana;
    }

    public void setMaxMana(long maxMana) {
        this.maxMana = maxMana;
    }

    public long getForaging() {
        return foraging;
    }

    public void setForaging(long foraging) {
        this.foraging = foraging;
    }

    public long getInventorySize() {
        return inventorySize;
    }

    public void setInventorySize(long inventorySize) {
        this.inventorySize = inventorySize;
    }

    public long getLockPicking() {
        return lockPicking;
    }

    public void setLockPicking(long lockPicking) {
        this.lockPicking = lockPicking;
    }

    private static double CONSTANT_MODIFIER = 0.02;

    public long getLevel() {
        double v = CONSTANT_MODIFIER * sqrt(getExperience());
        return (long) Math.floor(v);
    }

}
