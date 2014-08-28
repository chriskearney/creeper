package com.comandante.creeper.model;

import java.io.Serializable;
import java.util.Random;

public class Stats implements Serializable {
    int strength;
    int willpower;
    int  aim;
    int agile;
    int armorRating;
    int meleSkill;

    public Stats(int strength, int willpower, int aim, int agile, int armorRating, int meleSkill) {
        this.strength = strength;
        this.willpower = willpower;
        this.aim = aim;
        this.agile = agile;
        this.armorRating = armorRating;
        this.meleSkill = meleSkill;
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

    public static void main(String[] args) {
        Stats challenger = new Stats(7, 8, 6, 5, 4, 10);
        Stats victim = new Stats(7, 8, 6, 5, 4, 10);

        int damage = challenger.getStrength() + randInt(1, 6) - victim.getArmorRating();
        int chanceToHit = challenger.getStrength() + (challenger.getMeleSkill() * 5) - (victim.getAgile() * 5);

        System.out.println("DAMAGE: " + damage);
        System.out.println("CHANCETOHIT" + chanceToHit);
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


}
