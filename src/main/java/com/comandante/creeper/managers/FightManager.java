package com.comandante.creeper.managers;

import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.stat.Stats;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.room.RoomManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.server.ChannelUtils;

import java.util.Random;

public class FightManager {

    private final PlayerManager playerManager;
    private final RoomManager roomManager;
    private final EntityManager entityManager;
    private final ChannelUtils channelUtils;
    private static final Random random = new Random();

    public FightManager(ChannelUtils channelUtils, EntityManager entityManager, RoomManager roomManager, PlayerManager playerManager) {
        this.channelUtils = channelUtils;
        this.entityManager = entityManager;
        this.roomManager = roomManager;
        this.playerManager = playerManager;
    }

    public void fight(Player player, Npc npc) {
        PlayerMetadata playerMetadata = playerManager.getPlayerMetadata(player.getPlayerId());
        Stats npcStats = npc.getStats();
        Stats playerStats = playerMetadata.getStats();

        while (npcStats.getHealth() > 0) {
            if (playerStats.getHealth() <= 0) {
                break;
            }
            fightTurn(playerStats, npcStats, 3, player, npc);
        }

        playerManager.savePlayerMetadata(playerMetadata);

        if (playerStats.getHealth() <= 0) {
            channelUtils.write(player.getPlayerId(), "You died.");
            channelUtils.writeToRoom(player.getPlayerId(), player.getPlayerName() + " is now dead.");
            return;
        }

        if (npcStats.getHealth() <= 0) {
            channelUtils.writeNoPrompt(player.getPlayerId(), "You killed " + npc.getName());
            channelUtils.writeToRoom(player.getPlayerId(), npc.getDieMessage());
            entityManager.deleteNpcEntity(npc.getEntityId());
            return;
        }
    }

    public void fightTurn(Stats challenger, Stats victim, int numRoundsPerTurns, Player player, Npc npc) {
        for (int i = 0; i < numRoundsPerTurns; i++) {
            if (challenger.getHealth() <= 0 || victim.getHealth() <= 0) {
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
            channelUtils.writeNoPrompt(player.getPlayerId(), damageToVictim + " damage done to " + npc.getName());
        } else {
            channelUtils.writeNoPrompt(player.getPlayerId(), "You miss " + npc.getName());
        }
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (victim.getHealth() <= 0) {
            return;
        }
        int chanceToHitBack = getChanceToHit(victim, challenger);
        int damageBack = getAttack(victim, challenger);
        if (randInt(0, 100) < chanceToHitBack) {
            doPlayerDamage(player.getPlayerId(), damageBack);
            channelUtils.writeNoPrompt(player.getPlayerId(), npc.getName() + " damages you for " + damageBack);
        } else {
            channelUtils.writeNoPrompt(player.getPlayerId(), npc.getName() + " misses you");
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

}

