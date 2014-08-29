package com.comandante.creeper.stat;

import java.io.Serializable;

public class Stats implements Serializable {
    private int strength;
    private int willpower;
    private int aim;
    private int agile;
    private int armorRating;
    private int meleSkill;
    private int health;
    private int weaponRatingMax;
    private int weaponRatingMin;
    private int numberweaponOfRolls;

    public Stats(int strength,
                 int willpower,
                 int aim,
                 int agile,
                 int armorRating,
                 int meleSkill,
                 int health,
                 int weaponRatingMax,
                 int weaponRatingMin,
                 int numberweaponOfRolls) {
        this.strength = strength;
        this.willpower = willpower;
        this.aim = aim;
        this.agile = agile;
        this.armorRating = armorRating;
        this.meleSkill = meleSkill;
        this.health = health;
        this.weaponRatingMax = weaponRatingMax;
        this.weaponRatingMin = weaponRatingMin;
        this.numberweaponOfRolls = numberweaponOfRolls;
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

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
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

    public int getNumberweaponOfRolls() {
        return numberweaponOfRolls;
    }

    public void setNumberweaponOfRolls(int numberweaponOfRolls) {
        this.numberweaponOfRolls = numberweaponOfRolls;
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
                ", health=" + health +
                ", weaponRatingMax=" + weaponRatingMax +
                ", weaponRatingMin=" + weaponRatingMin +
                ", numberweaponOfRolls=" + numberweaponOfRolls +
                '}';
    }
}
