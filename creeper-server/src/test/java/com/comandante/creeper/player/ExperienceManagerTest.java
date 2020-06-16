package com.comandante.creeper.player;

import com.comandante.creeper.stats.experience.Experience;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class ExperienceManagerTest {

    private Experience experienceManager;

    @Before
    public void setUp() throws Exception {
        experienceManager = new Experience();
    }

    @Test
    public void testXp() throws Exception {
        int playerLevel = 20;
        Assert.assertEquals(0, experienceManager.calculateNpcXp(playerLevel, 13));
        Assert.assertEquals(0, experienceManager.calculateNpcXp(playerLevel, 12));
        Assert.assertEquals(232, experienceManager.calculateNpcXp(playerLevel, 23));
        Assert.assertEquals(203, experienceManager.calculateNpcXp(playerLevel, 22));
        Assert.assertEquals(174, experienceManager.calculateNpcXp(playerLevel, 21));
        Assert.assertEquals(145, experienceManager.calculateNpcXp(playerLevel, 20));
        Assert.assertEquals(132, experienceManager.calculateNpcXp(playerLevel, 19));
        Assert.assertEquals(119, experienceManager.calculateNpcXp(playerLevel, 18));
        int npcLevel = 3;
        for (int i = 0; i < 10; i++) {
            System.out.println("Player Level: " + i + " xp gained: " + experienceManager.calculateNpcXp(i, npcLevel));
        }
    }


    @Test
    public void testSpread() throws Exception {
        //System.out.println("Player Level: " + 28 + " " + experienceManager.getLevelColor(28, 25) + " " + experienceManager.calculateNpcXp(28, 25));
//        System.out.println("Player Level: " + 29 + " " + experienceManager.getLevelColor(29, 25) + " " + experienceManager.calculateNpcXp(29, 25));

        for (int i = 15; i < 30; i++) {
            System.out.println("Player Level: " + i + " " + experienceManager.getLevelColor(i, 25) + " " + experienceManager.calculateNpcXp(i, 25));
      }
    }
}