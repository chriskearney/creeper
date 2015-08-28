package com.comandante.creeper.player;

import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.server.Color;
import com.google.api.client.util.Maps;
import com.google.common.collect.Range;

import java.util.Iterator;
import java.util.Map;

public class ExperienceManager {

    public static final Map<Range<Long>, Long> rangeMap = Maps.newHashMap();

    public enum ExperienceType {
        RED(Color.RED),
        CYAN(Color.CYAN),
        YELLOW(Color.YELLOW),
        GREEN(Color.GREEN),
        GRAY(Color.WHITE);

        String color;

        ExperienceType(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }

    public static long getXp(Npc npc, Player player) {
        if (player.getLevel() == npc.getLevel()) {
            return getBaseXp(player.getLevel());
        } else if (player.getLevel() < npc.getLevel()) {
            return getHigherLevelNpcXp(npc.getLevel(), player.getLevel());
        } else {
            return getLowerLevelNpcXp(npc.getLevel(), player.getLevel());
        }
    }

    public static ExperienceType getExperienceType(Npc npc, Player player) {
        long npcLevel = npc.getLevel();
        long playerLevel = player.getLevel();
        if (npcLevel >= (playerLevel + 5)) {
            return ExperienceType.RED;
        } else if ((npcLevel == (playerLevel + 3)) || npcLevel == (playerLevel + 4)) {
            return ExperienceType.CYAN;
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
        if (playerLevel >= 1 && playerLevel <= 7) {
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

    public static long getBaseXp(long playerLevel) {
        return (playerLevel * 5) + 45;
    }

    public static long getHigherLevelNpcXp(long npcLevel, long playerLevel) {
        return (long) (getBaseXp(playerLevel) * (1 + .05 * (npcLevel - playerLevel)));
    }

    public static long getLowerLevelNpcXp(long npcLevel, long playerLevel) {
        if (getGrayLevel(playerLevel) >= npcLevel) {
            return 0;
        }
        return getBaseXp(playerLevel) * (1 - (playerLevel - npcLevel) / getZeroDifference(playerLevel));
    }

    public static void main(String[] args) {
        calculateXpRanges();
        for (int i = 1; i <= 80; i++) {
            System.out.println("Level: " + i + " Gray Level: " + getGrayLevel(i) + " zero-difference:" + getZeroDifference(i) + " xp to next level: " + getXpToNextLevel(i) + " total xp: " + totalXpNeeded(i));
       }
    }

    private static long getXpToNextLevel(long playerLevel) {
        if (playerLevel <= 10) {
            return (long) ((40 * Math.pow(playerLevel, 2)) + (360 * playerLevel));
        } else if (playerLevel >= 11 && playerLevel <= 30) {
            return (long) ((-.4 * Math.pow(playerLevel, 3)) + (40.4 * Math.pow(playerLevel, 2)) + (396 * playerLevel));
        } else if (playerLevel >= 31 && playerLevel <= 69) {
            return (long) ((65 * Math.pow(playerLevel, 2) - (165 * playerLevel) - 6750) * .82);
        } else if (playerLevel == 70) {
            return 155 + getBaseXp(playerLevel) * (1344 - 79 - ((79 - playerLevel) * (3 + (79 - playerLevel) * 4)));
        } else {
            return 155 + getBaseXp(playerLevel) * (1344 - ((79 - playerLevel) * (3 + (79 - playerLevel) * 4)));
        }
    }

    public static long totalXpNeeded(long playerLevel) {
        long totalXp = 0;
        if (playerLevel == 1) {
            return 0;
        }
        for (int i = 1; i <= playerLevel; i++) {
            totalXp += getXpToNextLevel(i);
        }
        return totalXp;
    }

    public static void calculateXpRanges() {
        for (int i = 1; i <= 80; i++) {
            rangeMap.put(Range.closedOpen(totalXpNeeded(i), totalXpNeeded(i + 1)), (long) i);
        }
    }

    public static long getLevel(long experience) {
        for (Map.Entry<Range<Long>, Long> next : rangeMap.entrySet()) {
            if (next.getKey().contains(experience)) {
                return next.getValue();
            }
        }
        return 80;
    }

    public static Range<Long> getRange(long experience) {
        for (Map.Entry<Range<Long>, Long> next : rangeMap.entrySet()) {
            if (next.getKey().contains(experience)) {
                return next.getKey();
            }
        }
        return Range.closedOpen(0L,0L);
    }

    public static long getXpToLevel(long experience) {
        return getRange(experience).upperEndpoint() - experience;
    }
}
