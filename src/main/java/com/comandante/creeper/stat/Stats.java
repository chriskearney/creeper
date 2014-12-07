package com.comandante.creeper.stat;

import java.io.Serializable;

public class Stats implements Serializable {
    private int strength;
    private int willpower;
    private int aim;
    private int agile;
    private int armorRating;
    private int meleSkill;
    private int currentHealth;
    private int maxHealth;
    private int weaponRatingMax;
    private int weaponRatingMin;
    private int numberOfWeaponRolls;
    private int experience;

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getWillpower() {
        return willpower;
    }

    public void setWillpower(int willpower) {
        this.willpower = willpower;
    }

    public int getAim() {
        return aim;
    }

    public void setAim(int aim) {
        this.aim = aim;
    }

    public int getAgile() {
        return agile;
    }

    public void setAgile(int agile) {
        this.agile = agile;
    }

    public int getArmorRating() {
        return armorRating;
    }

    public void setArmorRating(int armorRating) {
        this.armorRating = armorRating;
    }

    public int getMeleSkill() {
        return meleSkill;
    }

    public void setMeleSkill(int meleSkill) {
        this.meleSkill = meleSkill;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getWeaponRatingMax() {
        return weaponRatingMax;
    }

    public void setWeaponRatingMax(int weaponRatingMax) {
        this.weaponRatingMax = weaponRatingMax;
    }

    public int getWeaponRatingMin() {
        return weaponRatingMin;
    }

    public void setWeaponRatingMin(int weaponRatingMin) {
        this.weaponRatingMin = weaponRatingMin;
    }

    public int getNumberOfWeaponRolls() {
        return numberOfWeaponRolls;
    }

    public void setNumberOfWeaponRolls(int numberOfWeaponRolls) {
        this.numberOfWeaponRolls = numberOfWeaponRolls;
    }

    public Stats(int strength,
                 int willpower,
                 int aim,
                 int agile,
                 int armorRating,
                 int meleSkill,
                 int currentHealth,
                 int maxHealth,
                 int weaponRatingMax,
                 int weaponRatingMin,
                 int numberOfWeaponRolls,
                 int experience) {
        this.strength = strength;
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
    }

    @Override
    public String toString() {
        return "Stats{" +
                "strength=" + strength +
                ", willpower=" + willpower +
                ", aim=" + aim +
                ", agile=" + agile +
                ", armorRating=" + armorRating +
                ", meleSkill=" + meleSkill +
                ", currentHealth=" + currentHealth +
                ", maxHealth=" + maxHealth +
                ", weaponRatingMax=" + weaponRatingMax +
                ", weaponRatingMin=" + weaponRatingMin +
                ", numberOfWeaponRolls=" + numberOfWeaponRolls +
                '}';
    }
}
