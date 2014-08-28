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
    int weaponRating;
    int numberweaponOfRolls;

    public Stats(int strength, int willpower, int aim, int agile, int armorRating, int meleSkill, int health, int weaponRating, int numberweaponOfRolls) {
        this.strength = strength;
        this.willpower = willpower;
        this.aim = aim;
        this.agile = agile;
        this.armorRating = armorRating;
        this.meleSkill = meleSkill;
        this.health = health;
        this.weaponRating = weaponRating;
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

    public int getWeaponRating() {
        return weaponRating;
    }

    public void setWeaponRating(int weaponRating) {
        this.weaponRating = weaponRating;
    }

    public int getNumberweaponOfRolls() {
        return numberweaponOfRolls;
    }

    public void setNumberweaponOfRolls(int numberweaponOfRolls) {
        this.numberweaponOfRolls = numberweaponOfRolls;
    }

    public static void main(String[] args) throws InterruptedException {
        Stats challenger = new Stats(7, 8, 6, 5, 4, 10, 100, 20, 1);
        Stats victim = new Stats(7, 8, 6, 5, 4, 10, 100, 10, 1);


        int totalChallengerWin = 0;
        int totalVictimWin = 0;
        for (int i = 0; i < 30000; i++) {
            boolean results = fight(new Stats(7, 8, 6, 5, 4, 10, 100, 20, 1), new Stats(7, 8, 6, 5, 4, 10, 100, 10, 1));
            if (results) {
                totalChallengerWin++;
            } else {
                totalVictimWin++;
            }
        }

        System.out.println("\n\n\n\nChallenger: " + totalChallengerWin + " wins.");
        System.out.println("Victim: " + totalVictimWin + " wins.");



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
            turns++;
            damageToVictim = getAttack(challenger, victim);
            chanceToHitVictim = challenger.getStrength() + (challenger.getMeleSkill() * 5) - (victim.getAgile() * 5);
            if (randInt(0, 100) < chanceToHitVictim) {
                System.out.println("Attack landed on victim for : " + damageToVictim + " damage.");
                victim.setHealth(victim.getHealth() - damageToVictim);
                System.out.println("Victim has: " + victim.getHealth() + " health left.");
            } else {
                System.out.println("Miss!");
            }
            damageToChallenger = getAttack(challenger, victim);
            chanceToHitChallenger = challenger.getStrength() + (challenger.getMeleSkill() * 5) - (victim.getAgile() * 5);

            if (randInt(0, 100) < chanceToHitChallenger) {
                System.out.println("Attack landed on challenger for : " + damageToChallenger + " damage.");
                challenger.setHealth(challenger.getHealth() - damageToChallenger);
                System.out.println("Challenger has: " + challenger.getHealth() + " health left.");
            } else {
                System.out.println("Miss!");
            }
        }

        if (challenger.getHealth() > victim.getHealth()) {
            System.out.println("CHALLENGER WINS!");
            return true;
        } else {
            System.out.println("VICTIM WINS!");
            return false;

        }
    }

    private static int getAttack(Stats challenger, Stats victim) {
        int rolls = 0;
        int totDamage = 0;
        while (rolls <= challenger.getNumberweaponOfRolls()) {
            rolls++;
            totDamage = totDamage + randInt(1, challenger.getWeaponRating());
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
