package com.comandante.creeper.model;

import com.comandante.creeper.managers.EntityManager;
import com.comandante.creeper.managers.PlayerManager;
import com.comandante.creeper.managers.RoomManager;
import com.comandante.creeper.npc.Npc;
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
            return;
        }

        if (npcStats.getHealth() <= 0) {
            channelUtils.write(player.getPlayerId(), "You killed " + npc.getName());
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
            doDamage(victim, damageToVictim);
            channelUtils.writeNoPrompt(player.getPlayerId(), damageToVictim + " damage done to " + npc.getName());
        } else {
            channelUtils.writeNoPrompt(player.getPlayerId(), "You miss " + npc.getName());
        }
        if (victim.getHealth() <= 0) {
            return;
        }
        int chanceToHitBack = getChanceToHit(victim, challenger);
        int damageBack = getAttack(victim, challenger);
        if (randInt(0, 100) < chanceToHitBack) {
            doDamage(challenger, damageBack);
            channelUtils.writeNoPrompt(player.getPlayerId(), npc.getName() + " damages you for " + damageBack);
        } else {
            channelUtils.writeNoPrompt(player.getPlayerId(), npc.getName() + " misses you");
        }
    }

    private static void doDamage(Stats stats, int damageAmount) {
        stats.setHealth(stats.getHealth() - damageAmount);
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

