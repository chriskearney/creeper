package com.comandante.creeper.player;

import com.comandante.creeper.npc.Npc;

public class ExperienceManager {

    // http://wowwiki.wikia.com/wiki/Formulas:Mob_XP

    enum ExperienceType {
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        GRAY;
    }

    private ExperienceType getExperienceType(Npc npc, Player player) {
        long npcLevel = npc.getLevel();
        long playerLevel = player.getLevel();
        if (npcLevel >= (playerLevel + 5)) {
            return ExperienceType.RED;
        } else if ((npcLevel == (playerLevel + 3)) || npcLevel == (playerLevel + 4)) {
            return ExperienceType.ORANGE;
        } else if ((npcLevel == (playerLevel + 2)) || (npcLevel == (playerLevel - 2))) {
            return ExperienceType.YELLOW;
        } else if ((npcLevel <= (playerLevel - 3)) && npcLevel > getGrayLevel(playerLevel)) {
            return ExperienceType.GREEN;
        }
        return ExperienceType.GRAY;
    }

    public static long getGrayLevel(long playerLevel) {
        if (playerLevel >= 1 && playerLevel <= 5) {
            // All Mobs Give XP.
            return 0;
        } else if (playerLevel >= 6 && playerLevel <= 49) {
            double v = playerLevel - Math.floor(playerLevel / 10) - 5;
            return (long) v;
        } else if (playerLevel == 50) {
            return playerLevel - 10;
        } else if (playerLevel >= 51 && playerLevel <= 59) {
            double v = playerLevel - Math.floor(playerLevel / 5) - 1;
            return (long) v;
        } else if (playerLevel >= 60 && playerLevel <= 70) {
            return playerLevel - 9;
        } else {
            return 0;
        }
    }

    private static long getZeroDifference(long playerLevel) {
        if (playerLevel >= 1 && playerLevel <= 7){
            return 5;
        } else if (playerLevel >= 8 && playerLevel <= 9) {
            return 6;
        } else if (playerLevel >= 10 && playerLevel <= 11) {
            return 7;
        } else if (playerLevel >= 12 && playerLevel <= 15) {
            return 8;
        } else if (playerLevel >= 16 && playerLevel <= 19) {
            return 9;
        } else if (playerLevel >= 20 && playerLevel <= 29) {
            return 11;
        } else if (playerLevel >= 30 && playerLevel <= 39) {
            return 12;
        } else if (playerLevel >= 40 && playerLevel <= 44) {
            return 13;
        } else if (playerLevel >= 45 && playerLevel <= 49) {
            return 14;
        } else if (playerLevel >= 50 && playerLevel <= 54) {
            return 15;
        } else if (playerLevel >= 55 && playerLevel <= 59) {
            return 16;
        } else if (playerLevel >= 60 && playerLevel <= 79) {
            return 17;
        } else {
            return 18;
        }

    }


    public static void main(String[] args) {
        for (int i = 0; i < 70; i++) {
            System.out.println("Level: " + i + " Gray Level: " + getGrayLevel(i) + " zero-difference:" + getZeroDifference(i) );
        }
    }

    //public long getBasicExperienceGained(Npc npc, Player player) {
     //   long level = player.getLevel();

//    }

}
