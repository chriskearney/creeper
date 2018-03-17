package com.comandante.creeper.items;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;
import com.google.common.collect.Sets;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static java.lang.StrictMath.sqrt;

public class LockPickingManager {

    private final GameManager gameManager;
    private final Random random = new Random();

    private static double LOCKPICKING_EXP_CONSTANT_MODIFIER = 0.05;
    private static double LOCKPICKING_LEVEL_PCT_BOOST_MODIFIER = .5;

    public LockPickingManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public Set<Item> pickChestLock(Player player, Item chest) {
        long modifiedLockingPickingLevel = getLockPickingLevel(player.getPlayerStatsWithEquipmentAndLevel().getLockPicking());
        double pctSuccessBoostForLevel = getPctSuccessBoostForLevel(modifiedLockingPickingLevel);
        double lockPickingPctOfSuccess = (chest.getLockPickingDifficulty().getPctSuccess() * pctSuccessBoostForLevel) + chest.getLockPickingDifficulty().getPctSuccess();
        if (!getRandPercent(lockPickingPctOfSuccess)) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "Attempt to pick lock on " + chest.getItemName() + " failed.\r\n");
            return Sets.newHashSet();
        }
        long goldAmountReturn = gameManager.getLootManager().lootGoldAmountReturn(chest.getLoot());
        Set<Item> items = gameManager.getLootManager().lootItemsReturn(chest.getLoot());
        // Destroy chest
        player.removeInventoryId(chest.getItemId());
        gameManager.getEntityManager().removeItem(chest);
        if (items.isEmpty()) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "The " + chest.getItemName() + " was empty.\r\n");
            return Sets.newHashSet();
        }
        return items;
    }

    private void canPickLock(Stats stats, LockPickingDifficulty lockPickingDifficulty) {}


    public static long getLockPickingLevel(long lockPickingStat) {
        double v = LOCKPICKING_EXP_CONSTANT_MODIFIER * sqrt(lockPickingStat);
        return (long) Math.floor(v);
    }

    public static double getPctSuccessBoostForLevel(long level) {
        return LOCKPICKING_LEVEL_PCT_BOOST_MODIFIER * sqrt(level);
    }

    private boolean getRandPercent(double percent) {
        double rangeMin = 0;
        double rangeMax = 100;
        double randomValue = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
        return randomValue <= percent;
    }
}
