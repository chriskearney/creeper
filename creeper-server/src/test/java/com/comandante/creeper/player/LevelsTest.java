package com.comandante.creeper.player;

import com.comandante.creeper.stats.Levels;
import org.junit.Assert;
import org.junit.Test;

public class LevelsTest {

    @Test
    public void testLevels() throws Exception {
        for (int i = 0; i < 100; i++) {
            long xp = Levels.getXp(i);
            long level = Levels.getLevel(xp);
            Assert.assertEquals(level, i);
            System.out.println("Level: " + i + " | XP: " + xp);
        }
    }

    @Test
    public void printHowMuchXpIsNecessaryToLevelUp() throws Exception {
        for (int i = 0; i < 100; i++) {
            long xp = Levels.getXp(i);
            long level = Levels.getLevel(xp);
            long xpNext = Levels.getXp(i + 1);
            long l = xpNext - xp;
            System.out.println("Level " + i + " to " + (i + 1) + " takes " + l + " xp.");
        }
    }
}