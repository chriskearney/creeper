package com.comandante.creeper.items;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.StatsChange;
import com.comandante.creeper.npc.NpcStatsChangeBuilder;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.stats.StatsBuilder;
import com.comandante.creeper.stats.StatsHelper;
import org.apache.log4j.Logger;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public class EffectsManager {

    private final GameManager gameManager;

    private static final Logger log = Logger.getLogger(EffectsManager.class);

    public EffectsManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void applyEffectsToNpcs(Player player, Set<Npc> npcs, Set<Effect> effects) {
        effects.forEach(effect ->
                npcs.forEach(npc -> {
                    Effect nEffect = new Effect(effect);
                    nEffect.setPlayerId(player.getPlayerId());
                    if (effect.getDurationStats().getCurrentHealth() < 0) {
                        log.error("ERROR! Someone added an effect with a health modifier which won't work for various reasons.");
                        return;
                    }
                    StatsHelper.combineStats(npc.getStats(), effect.getDurationStats());
                    npc.addEffect(nEffect);
                }));
    }

    public void applyEffectsToPlayer(Player destinationPlayer, Player player, Set<Effect> effects) {
        for (Effect effect : effects) {
            Effect nEffect = new Effect(effect);
            nEffect.setPlayerId(player.getPlayerId());
            if (effect.getDurationStats().getCurrentHealth() < 0) {
                log.error("ERROR! Someone added an effect with a health modifier which won't work for various reasons.");
                continue;
            }
            String effectApplyMessage;
            if (destinationPlayer.addEffect(effect)) {
                effectApplyMessage = Color.BOLD_ON + Color.GREEN + "[effect] " + Color.RESET + nEffect.getEffectName() + " applied!" + "\r\n";
                gameManager.getChannelUtils().write(destinationPlayer.getPlayerId(), effectApplyMessage);
            } else {
                effectApplyMessage = Color.BOLD_ON + Color.GREEN + "[effect] " + Color.RESET + Color.RED + "Unable to apply " + nEffect.getEffectName() + "!" + "\r\n";
                gameManager.getChannelUtils().write(player.getPlayerId(), effectApplyMessage);
            }
        }
    }

    public void application(Effect effect, Player player) {
        // if there are effecst that modify player health, deal with it here, you can't rely on combine stats.
        Stats applyStatsOnTick = effect.getApplyStatsOnTick();
        if (effect.getApplyStatsOnTick() != null) {
            if (effect.getApplyStatsOnTick().getCurrentHealth() != 0) {
                gameManager.getPlayerManager().getPlayer(player.getPlayerId()).updatePlayerHealth(effect.getApplyStatsOnTick().getCurrentHealth(), null);
                for (String message : effect.getEffectApplyMessages()) {
                    if (effect.getApplyStatsOnTick().getCurrentHealth() > 0) {
                        gameManager.getChannelUtils().write(player.getPlayerId(), Color.BOLD_ON + Color.GREEN + "[effect] " + Color.RESET + message + " +" + Color.GREEN + NumberFormat.getNumberInstance(Locale.US).format(effect.getApplyStatsOnTick().getCurrentHealth()) + Color.RESET + "\r\n", true);
                    } else {
                        gameManager.getChannelUtils().write(player.getPlayerId(), Color.BOLD_ON + Color.GREEN + "[effect] " + Color.RESET + message + " -" + Color.RED + NumberFormat.getNumberInstance(Locale.US).format(effect.getApplyStatsOnTick().getCurrentHealth()) + Color.RESET + "\r\n", true);
                    }
                }
                //applyStatsOnTick = new StatsBuilder(applyStatsOnTick).setCurrentHealth(0).createStats();
            }
            //  StatsHelper.combineStats(playerMetadata.getTargetStatsChange(), applyStatsOnTick);
        }
    }

    public void application(Effect effect, Npc npc) {
        Player player = gameManager.getPlayerManager().getPlayer(effect.getPlayerId());
        Stats applyStats = new Stats(effect.getApplyStatsOnTick());
        // if there are effecst that modify npc health, deal with it here, you can't rely on combine stats.
        if (effect.getApplyStatsOnTick().getCurrentHealth() < 0) {
            String s = Color.BOLD_ON + Color.GREEN + "[effect] " + Color.RESET + npc.getColorName() + " is affected by " + effect.getEffectDescription() + " " + Color.RED + applyStats.getCurrentHealth() + Color.RESET + Color.CYAN + Color.RESET;
            StatsChange npcStatsChange = new NpcStatsChangeBuilder()
                    .setStats(applyStats)
                    .setDamageStrings(Arrays.asList(s))
                    .setPlayer(player)
                    .createNpcStatsChange();
            npc.addNpcDamage(npcStatsChange);
        }
        // Remove any health mods, as it will screw things up.
        Stats finalCombineWorthyStats = new StatsBuilder(applyStats).setCurrentHealth(0).createStats();
        StatsHelper.combineStats(npc.getStats(), finalCombineWorthyStats);
    }

    public void removeDurationStats(Effect effect, Npc npc) {
        Stats newStats = new Stats(effect.getDurationStats());
        StatsHelper.inverseStats(newStats);
        StatsHelper.combineStats(npc.getStats(), newStats);
    }

    public void removeDurationStats(Effect effect, PlayerMetadata playerMetadata) {
        Stats newStats = new Stats(effect.getDurationStats());
        StatsHelper.inverseStats(newStats);
        StatsHelper.combineStats(playerMetadata.getStats(), newStats);
    }
}
