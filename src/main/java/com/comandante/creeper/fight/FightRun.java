package com.comandante.creeper.fight;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.stat.Stats;

import java.util.concurrent.Callable;


public class FightRun implements Callable<FightResults> {

    private final Player player;
    private final Npc npc;
    private final GameManager gameManager;

    public FightRun(Player player, Npc npc, GameManager gameManager) {
        this.player = player;
        this.npc = npc;
        this.gameManager = gameManager;
    }

    @Override
    public FightResults call() throws Exception {
        Stats npcStats = npc.getStats();
        PlayerMetadata playerMetadata = gameManager.getPlayerManager().getPlayerMetadata(player.getPlayerId());
        Stats playerStats = gameManager.getPlayerManager().getPlayerMetadata(player.getPlayerId()).getStats();

        while (npcStats.getCurrentHealth() > 0) {
            if (playerStats.getCurrentHealth() <= 0) {
                break;
            }
            gameManager.getFightManager().fightTurn(playerStats, npcStats, 3, player, npc);
            gameManager.getChannelUtils().write(player.getPlayerId(), "Use an ability!");
            gameManager.getPlayerManager().getSessionManager().getSession(player.getPlayerId()).setIsAbleToDoAbility(true);
            try {
                Thread.sleep(2200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gameManager.getPlayerManager().getSessionManager().getSession(player.getPlayerId()).setIsAbleToDoAbility(false);
        }

        gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);

        FightResults fightResults = new FightResultsBuilder().setNpcWon(false).setPlayerWon(false).createFightResults();


        if (playerStats.getCurrentHealth() <= 0) {
            gameManager.getChannelUtils().write(player.getPlayerId(), "You died.");
            gameManager.getChannelUtils().writeToRoom(player.getPlayerId(), player.getPlayerName() + " is now dead.");
            npc.setIsInFight(false);
            fightResults = new FightResultsBuilder().setNpcWon(true).setPlayerWon(false).createFightResults();
        }

        if (npcStats.getCurrentHealth() <= 0) {
            gameManager.getChannelUtils().writeNoPrompt(player.getPlayerId(), "You killed " + npc.getName());
            gameManager.getChannelUtils().writeToRoom(player.getPlayerId(), npc.getDieMessage());
            gameManager.getEntityManager().deleteNpcEntity(npc.getEntityId());
            fightResults = new FightResultsBuilder().setNpcWon(false).setPlayerWon(true).createFightResults();
        }
        gameManager.getChannelUtils().writeOnlyPrompt(player.getPlayerId());
        return fightResults;
    }
}
