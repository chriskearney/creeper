package com.comandante.creeper.model;

import java.io.Serializable;
import java.util.Random;

public class Stats implements Serializable {
    int strength;
    int willpower;
    int aim;
    int agile;
    int armorRating;
    int meleSkill;
    int health;
    int weaponRatingMax;
    int weaponRatingMin;
    int numberweaponOfRolls;

    public static int NO_TURNS = 0;
    public static int NO_HITS_CHALLENGER = 0;
    public static int NO_HITS_VICTIM = 0;
    public static int NO_MISSES_CHALLENGER = 0;
    public static int NO_MISSES_VICTIM = 0;

    public Stats(int strength, int willpower, int aim, int agile, int armorRating, int meleSkill, int health, int weaponRatingMin, int weaponRatingMax, int numberweaponOfRolls) {
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

    public Stats() {
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

    public static void main(String[] args) throws InterruptedException {
        int totalChallengerWin = 0;
        int totalVictimWin = 0;
        for (int i = 0; i < 30000; i++) {
            boolean results = fight(new Stats(10, 2, 2, 2, 2, 10, 100, 10, 20, 1), new Stats(5, 1, 1, 1, 5, 1, 100, 5, 10, 1));

            //strength, willpower, aim, agile, armorRating, meleSkill, health, weaponRatingMin, weaponRatingMax, numberweaponOfRolls
            if (results) {
                totalChallengerWin++;
            } else {
                totalVictimWin++;
            }
        }

        System.out.println("Challenger: " + totalChallengerWin + " wins.");
        System.out.println("Victim: " + totalVictimWin + " wins.");
        System.out.println("AVERAGE TURNS: " + NO_TURNS / 30000);
        System.out.println("AVERAGE HITS CHALLENGER: " + NO_HITS_CHALLENGER / 30000);

        System.out.println("AVERAGE MISSES CHALLENGER: " + NO_MISSES_CHALLENGER / 30000);

        System.out.println("AVERAGE HITS VICTIM: " + NO_HITS_VICTIM / 30000);

        System.out.println("AVERAGE MISSES VICTIM: " + NO_MISSES_VICTIM / 30000);




    }

    public static boolean fight(Stats challenger, Stats victim) {
        int damageToVictim = 0;
        int chanceToHitVictim = 0;


        int damageToChallenger = 0;
        int chanceToHitChallenger = 0;


        int turns = 0;
        while (true) {
            if (challenger.getHealth() <= 0 || victim.getHealth() <= 0) {
                break;
            }
            NO_TURNS++;
            damageToVictim = getAttack(challenger, victim);
            chanceToHitVictim = (challenger.getStrength() + challenger.getMeleSkill()) * 5 - victim.getAgile() * 5;
            if (randInt(0, 100) < chanceToHitVictim) {
           //     System.out.println("Attack landed on victim for : " + damageToVictim + " damage.");
                victim.setHealth(victim.getHealth() - damageToVictim);
           //     System.out.println("Victim has: " + victim.getHealth() + " health left.");
                NO_HITS_CHALLENGER++;
            } else {
               // System.out.println("Miss!");
                NO_MISSES_CHALLENGER++;
            }
            damageToChallenger = getAttack(victim, challenger);
            chanceToHitChallenger = (victim.getStrength() + victim.getMeleSkill()) * 5 - (challenger.getAgile() * 5);

            if (randInt(0, 100) < chanceToHitChallenger) {
             //   System.out.println("Attack landed on challenger for : " + damageToChallenger + " damage.");
                challenger.setHealth(challenger.getHealth() - damageToChallenger);
                NO_HITS_VICTIM++;
               // System.out.println("Challenger has: " + challenger.getHealth() + " health left.");
            } else {
                //System.out.println("Miss!");
                NO_MISSES_VICTIM++;
            }
        }

        if (challenger.getHealth() > victim.getHealth()) {
            //System.out.println("CHALLENGER WINS!");
            return true;
        } else {
          //  System.out.println("VICTIM WINS!");
            return false;

        }
    }

    private static int getAttack(Stats challenger, Stats victim) {
        int rolls = 0;
        int totDamage = 0;
        while (rolls <= challenger.getNumberweaponOfRolls()) {
            rolls++;
            totDamage = totDamage + randInt(challenger.getWeaponRatingMin(), challenger.getWeaponRatingMax());
        }
        return challenger.getStrength() + totDamage - victim.getArmorRating();
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
