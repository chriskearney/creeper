package com.comandante.creeper.model;

import java.util.Random;

public class StatsTester {

    public static int NO_TURNS = 0;
    public static int NO_HITS_CHALLENGER = 0;
    public static int NO_HITS_VICTIM = 0;
    public static int NO_MISSES_CHALLENGER = 0;
    public static int NO_MISSES_VICTIM = 0;
    public static int TOTAL_DAMAGE_CHALLENGER = 0;
    public static int TOTAL_DAMAGE_VICTIM = 0;


    public static void main(String[] args) throws InterruptedException {
        int totalChallengerWin = 0;
        int totalVictimWin = 0;
        int NUM_EXECUTION = 60000;
        for (int i = 0; i < NUM_EXECUTION; i++) {
            boolean results = fight(
                    PlayerStats.DEFAULT_PLAYER.createStats(),
                    NpcStats.JOE_NPC.createStats());
            //strength, willpower, aim, agile, armorRating, meleSkill, health, weaponRatingMin, weaponRatingMax, numberweaponOfRolls
            if (results) {
                totalChallengerWin++;
            } else {
                totalVictimWin++;
            }
        }
        System.out.println("Challenger: " + totalChallengerWin + " wins.");
        System.out.println("Victim: " + totalVictimWin + " wins.");
        System.out.println("AVERAGE TURNS: " + NO_TURNS / NUM_EXECUTION);
        System.out.println("AVERAGE HITS CHALLENGER: " + NO_HITS_CHALLENGER / NUM_EXECUTION);
        System.out.println("AVERAGE MISSES CHALLENGER: " + NO_MISSES_CHALLENGER / NUM_EXECUTION);
        System.out.println("AVERAGE DAMAGE CHALLENGER: " + TOTAL_DAMAGE_CHALLENGER / NUM_EXECUTION);
        System.out.println("AVERAGE HITS VICTIM: " + NO_HITS_VICTIM / NUM_EXECUTION);
        System.out.println("AVERAGE MISSES VICTIM: " + NO_MISSES_VICTIM / NUM_EXECUTION);
        System.out.println("AVERAGE DAMAGE VICTIM: " + TOTAL_DAMAGE_VICTIM / NUM_EXECUTION);

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
                victim.setHealth(victim.getHealth() - damageToVictim);
                TOTAL_DAMAGE_CHALLENGER = TOTAL_DAMAGE_CHALLENGER + damageToVictim;
                NO_HITS_CHALLENGER++;
            } else {
                NO_MISSES_CHALLENGER++;
            }
            damageToChallenger = getAttack(victim, challenger);
            chanceToHitChallenger = (victim.getStrength() + victim.getMeleSkill()) * 5 - (challenger.getAgile() * 5);

            if (randInt(0, 100) < chanceToHitChallenger) {
                challenger.setHealth(challenger.getHealth() - damageToChallenger);
                TOTAL_DAMAGE_VICTIM = TOTAL_DAMAGE_VICTIM + damageToVictim;
                NO_HITS_VICTIM++;
            } else {
                NO_MISSES_VICTIM++;
            }
        }

        if (challenger.getHealth() > victim.getHealth()) {
            return true;
        } else {
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
