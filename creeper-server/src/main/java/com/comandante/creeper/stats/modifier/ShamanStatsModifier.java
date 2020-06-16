package com.comandante.creeper.stats.modifier;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.stats.DefaultStats;
import com.comandante.creeper.stats.Levels;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.stats.StatsBuilder;

import java.util.Optional;

import static java.lang.Math.pow;
import static java.lang.StrictMath.sqrt;

public class ShamanStatsModifier implements StatsModifier {

    private final GameManager gameManager;

    public ShamanStatsModifier(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    private static double MELE_CONSTANT_MODIFIER = .4;
    private static double WILLPOWER_CONSTANT_MODIFIER = .9;
    private static double INTELLIGENCE_CONSTANT_MODIFIER = .7;
    private static double AGILE_CONSTANT_MODIFIER = .3;
    private static double AIM_CONSTANT_MODIFIER = .4;
    private static double HEALTH_CONSTANT_MODIFIER = 2;
    private static double ARMOR_CONSTANT_MODIFIER = .4;
    private static double STRENGTH_CONSTANT_MODIFIER = .4;
    private static double MANA_CONSTANT_MODIFIER = 5;

    public static long getMeleForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, MELE_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getWillpowerForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, WILLPOWER_CONSTANT_MODIFIER));
        return (long) Math.floor(v) + baseStat;
    }

    public static long getIntelligenceForLevel(long baseStat, long level) {
        double v = (level) * sqrt(pow(level, INTELLIGENCE_CONSTANT_MODIFIER));
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
    public Stats modify(Player player) {
        Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(player.getPlayerId());
        if (!playerMetadataOptional.isPresent()) {
            return DefaultStats.DEFAULT_PLAYER.createStats();
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        Stats baseStats = playerMetadata.getStats();
        long level = Levels.getLevel(baseStats.getExperience());
        long newMaxHealth = getHealthForLevel(baseStats.getMaxHealth(), level);
        long newArmorRating = getArmorForLevel(baseStats.getArmorRating(), level);
        long newStrengthRating = getStrengthForLevel(baseStats.getStrength(), level);
        long newMaxMana = getManaForLevel(baseStats.getMaxMana(), level);
        long newAimRating = getAimForLevel(baseStats.getAim(), level);
        long newWillpowerRating = getWillpowerForLevel(baseStats.getWillpower(), level);
        long newIntelligenceRating = getIntelligenceForLevel(baseStats.getIntelligence(), level);
        long newAgileRating = getAgileForLevel(baseStats.getAgile(), level);
        long newMeleRating = getMeleForLevel(baseStats.getMeleSkill(), level);
        StatsBuilder statsBuilder = new StatsBuilder(baseStats);
        statsBuilder.setMaxHealth(newMaxHealth);
        statsBuilder.setArmorRating(newArmorRating);
        statsBuilder.setStrength(newStrengthRating);
        statsBuilder.setIntelligence(newIntelligenceRating);
        statsBuilder.setMaxMana(newMaxMana);
        statsBuilder.setAim(newAimRating);
        statsBuilder.setWillpower(newWillpowerRating);
        statsBuilder.setAgile(newAgileRating);
        statsBuilder.setMeleSkill(newMeleRating);
        statsBuilder.setCurrentHealth(baseStats.getCurrentHealth());
        statsBuilder.setCurrentMana(baseStats.getCurrentMana());
        return statsBuilder.createStats();
    }
}