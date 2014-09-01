package com.comandante.creeper.fight;

import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.Color;
import com.comandante.creeper.server.CreeperSession;
import com.comandante.creeper.stat.Stats;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FightManager {

    private final PlayerManager playerManager;
    private final EntityManager entityManager;
    private final ChannelUtils channelUtils;
    private static final Random random = new Random();

    private final ExecutorService fightService;

    public FightManager(ChannelUtils channelUtils, EntityManager entityManager, PlayerManager playerManager) {
        this.channelUtils = channelUtils;
        this.entityManager = entityManager;
        this.playerManager = playerManager;
        this.fightService = Executors.newFixedThreadPool(10);
    }


    public Future<FightResults> fight(FightRun fightRun) {
        return fightService.submit(fightRun);
    }

    public void fightTurn(Stats challenger, Stats victim, int numRoundsPerTurns, Player player, Npc npc) {
        for (int i = 0; i < numRoundsPerTurns; i++) {
            if (challenger.getCurrentHealth() <= 0 || victim.getCurrentHealth() <= 0) {
                return;
            }
            fightRound(challenger, victim, player, npc);
        }
    }

    public void fightRound(Stats challenger, Stats victim, Player player, Npc npc) {
        int chanceToHit = getChanceToHit(challenger, victim);
        int damageToVictim = 0;
        if (randInt(0, 100) < chanceToHit) {
            damageToVictim = getAttack(challenger, victim);
        }
        if (damageToVictim > 0) {
            doNpcDamage(npc.getEntityId(), damageToVictim);
            channelUtils.writeNoPromptNoAfterSpace(player.getPlayerId(), damageToVictim + Color.BOLD_ON + Color.RED + " damage" + Color.RESET + " done to " + npc.getColorName());
        } else {
            channelUtils.writeNoPromptNoAfterSpace(player.getPlayerId(), "You miss " + npc.getName());
        }
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (victim.getCurrentHealth() <= 0) {
            return;
        }
        int chanceToHitBack = getChanceToHit(victim, challenger);
        int damageBack = getAttack(victim, challenger);
        if (randInt(0, 100) < chanceToHitBack) {
            doPlayerDamage(player.getPlayerId(), damageBack);
            channelUtils.writeNoPromptNoAfterSpace(player.getPlayerId(), npc.getColorName() + Color.BOLD_ON + Color.RED + " damages" + Color.RESET + " you for " + damageBack);
        } else {
            channelUtils.writeNoPromptNoAfterSpace(player.getPlayerId(), npc.getColorName() + " misses you");
        }
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doPlayerDamage(String playerId, int damageAmount) {
        playerManager.updatePlayerHealth(playerId, -damageAmount);
    }

    private void doNpcDamage(String npcId, int damageAmount) {
        entityManager.updateNpcHealth(npcId, -damageAmount);
    }

    private static int getChanceToHit(Stats challenger, Stats victim) {
        return (challenger.getStrength() + challenger.getMeleSkill()) * 5 - victim.getAgile() * 5;
    }

    private static int getAttack(Stats challenger, Stats victim) {
        int rolls = 0;
        int totDamage = 0;
        while (rolls <= challenger.getNumberweaponOfRolls()) {
            rolls++;
            totDamage = totDamage + randInt(challenger.getWeaponRatingMin(), challenger.getWeaponRatingMax());
        }
        return challenger.getStrength() + totDamage - victim.getArmorRating();
    }

    private static int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static boolean isActiveFight(CreeperSession session) {
        if (session == null) return false;
        return (session.getActiveFight().isPresent() && !session.getActiveFight().get().isDone());
    }

}

