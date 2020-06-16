package com.comandante.creeper.stats.modifier;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;

public class StatsModifierFactory {

    private final GameManager gameManager;

    public StatsModifierFactory(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public Stats getStatsModifier(Player player) {
        StatsModifier modifer = new BasicPlayerLevelStatsModifier(gameManager);
        switch (player.getPlayerClass()) {
            case WARRIOR:
                modifer = new WarriorStatsModifier(gameManager);
                break;
            case WIZARD:
                modifer = new WizardStatsModifier(gameManager);
                break;
            case RANGER:
                modifer = new RangerStatsModifier(gameManager);
                break;
            case SHAMAN:
                modifer = new ShamanStatsModifier(gameManager);
                break;
            default:
                modifer = new BasicPlayerLevelStatsModifier(gameManager);
                break;

        }
        return modifer.modify(player);
    }
}
