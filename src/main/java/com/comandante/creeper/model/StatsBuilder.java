package com.comandante.creeper.model;

public class StatsBuilder {
    private int strength;
    private int willpower;
    private int aim;
    private int agile;
    private int armorRating;
    private int meleSkill;
    private int health;
    private int weaponRatingMin;
    private int weaponRatingMax;
    private int numberweaponOfRolls;

    public StatsBuilder setStrength(int strength) {
        this.strength = strength;
        return this;
    }

    public StatsBuilder setWillpower(int willpower) {
        this.willpower = willpower;
        return this;
    }

    public StatsBuilder setAim(int aim) {
        this.aim = aim;
        return this;
    }

    public StatsBuilder setAgile(int agile) {
        this.agile = agile;
        return this;
    }

    public StatsBuilder setArmorRating(int armorRating) {
        this.armorRating = armorRating;
        return this;
    }

    public StatsBuilder setMeleSkill(int meleSkill) {
        this.meleSkill = meleSkill;
        return this;
    }

    public StatsBuilder setHealth(int health) {
        this.health = health;
        return this;
    }

    public StatsBuilder setWeaponRatingMin(int weaponRatingMin) {
        this.weaponRatingMin = weaponRatingMin;
        return this;
    }

    public StatsBuilder setWeaponRatingMax(int weaponRatingMax) {
        this.weaponRatingMax = weaponRatingMax;
        return this;
    }

    public StatsBuilder setNumberweaponOfRolls(int numberweaponOfRolls) {
        this.numberweaponOfRolls = numberweaponOfRolls;
        return this;
    }

    public Stats createStats() {
        return new Stats(strength, willpower, aim, agile, armorRating, meleSkill, health, weaponRatingMin, weaponRatingMax, numberweaponOfRolls);
    }
}