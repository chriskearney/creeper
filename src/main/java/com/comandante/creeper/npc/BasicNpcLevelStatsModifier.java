package com.comandante.creeper.npc;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Levels;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.player.StatsModifier;
import com.comandante.creeper.stat.Stats;
import com.comandante.creeper.stat.StatsBuilder;

import static java.lang.Math.pow;
import static java.lang.StrictMath.sqrt;


public class BasicNpcLevelStatsModifier  implements StatsModifier {

    private final GameManager gameManager;

    public BasicNpcLevelStatsModifier(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private static double MELE_CONSTANT_MODIFIER = 1.01;
    private static double WILLPOWER_CONSTANT_MODIFIER = 1.01;
    private static double AGILE_CONSTANT_MODIFIER = 1.01;
    private static double AIM_CONSTANT_MODIFIER = 1.01;
    private static double HEALTH_CONSTANT_MODIFIER = 4;
    private static double ARMOR_CONSTANT_MODIFIER = 1.01;
    private static double STRENGTH_CONSTANT_MODIFIER = 1.4;
    private static double MANA_CONSTANT_MODIFIER = 2;

    public static long getMeleForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, MELE_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getWillpowerForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, WILLPOWER_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getAgileForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, AGILE_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getAimForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, AIM_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getStrengthForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, STRENGTH_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getArmorForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, ARMOR_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getHealthForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, HEALTH_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getManaForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, MANA_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    @Override
    public Stats modify(Npc npc) {
        Stats baseStats = npc.getBaseStats();
        long level = Levels.getLevel(baseStats.getExperience());
        long newMaxHealth = getHealthForLevel(baseStats.getMaxHealth(), level);
        long newArmorRating = getArmorForLevel(baseStats.getArmorRating(), level);
        long newStrengthRating = getStrengthForLevel(baseStats.getStrength(), level);
        long newMaxMana = getManaForLevel(baseStats.getMaxMana(), level);
        long newAimRating = getAimForLevel(baseStats.getAim(), level);
        long newWillpowerRating = getWillpowerForLevel(baseStats.getWillpower(), level);
        long newAgileRating = getAgileForLevel(baseStats.getAgile(), level);
        long newMeleRating = getMeleForLevel(baseStats.getMeleSkill(), level);
        StatsBuilder statsBuilder = new StatsBuilder(baseStats);
        statsBuilder.setMaxHealth(newMaxHealth);
        statsBuilder.setArmorRating(newArmorRating);
        statsBuilder.setStrength(newStrengthRating);
        statsBuilder.setMaxMana(newMaxMana);
        statsBuilder.setAim(newAimRating);
        statsBuilder.setWillpower(newWillpowerRating);
        statsBuilder.setAgile(newAgileRating);
        statsBuilder.setMeleSkill(newMeleRating);
        statsBuilder.setCurrentHealth(baseStats.getCurrentHealth());
        statsBuilder.setCurrentMana(baseStats.getCurrentMana());
        return statsBuilder.createStats();
    }

    @Override
    public Stats modify(Player player) {
        return null;
    }
}