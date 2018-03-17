package com.comandante.creeper.items;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;

import java.util.Optional;

public class LockPickingManager {

    private final GameManager gameManager;

    public LockPickingManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }


    public Optional<Loot> pickChestLock(Player player, Item chest) {
        LockPickingDifficulty lockPickingDifficulty = chest.getLockPickingDifficulty();
        Stats modifiedPlayerStats = player.getPlayerStatsWithEquipmentAndLevel();

    }
}
