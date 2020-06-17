package com.comandante.creeper.items;


import com.comandante.creeper.stats.Stats;

import java.util.List;

public class Effect {

    private String effectName;
    private String effectDescription;
    private List<String> effectApplyMessages;
    private Stats applyStatsOnTick;
    private Stats durationStats;
    private int maxEffectApplications;
    private boolean frozenMovement;
    private int effectApplications;
    private String playerId;

    public Effect() {
    }

    public Effect(String effectName, String effectDescription, List<String> effectApplyMessages, Stats applyStatsOnTick, Stats durationStats, int maxEffectApplications, boolean frozenMovement) {
        this.effectName = effectName;
        this.effectDescription = effectDescription;
        this.effectApplyMessages = effectApplyMessages;
        this.applyStatsOnTick = applyStatsOnTick;
        this.durationStats = durationStats;
        this.maxEffectApplications = maxEffectApplications;
        this.frozenMovement = frozenMovement;
        this.effectApplications = 0;
    }

    public Effect(Effect effect) {
        this.effectName = effect.effectName;
        this.effectDescription = effect.effectDescription;
        this.effectApplyMessages = effect.effectApplyMessages;
        this.applyStatsOnTick = effect.applyStatsOnTick;
        this.durationStats = effect.durationStats;
        this.maxEffectApplications = effect.maxEffectApplications;
        this.frozenMovement = effect.frozenMovement;
        this.effectApplications = effect.effectApplications;
    }


    public String getEffectName() {
        return effectName;
    }

    public String getEffectDescription() {
        return effectDescription;
    }

    public List<String> getEffectApplyMessages() {
        return effectApplyMessages;
    }

    public Stats getApplyStatsOnTick() {
        return applyStatsOnTick;
    }

    public int getMaxEffectApplications() {
        return maxEffectApplications;
    }

    public boolean isFrozenMovement() {
        return frozenMovement;
    }

    public int getEffectApplications() {
        return effectApplications;
    }

    public void setEffectApplications(int effectApplications) {
        this.effectApplications = effectApplications;
    }

    public Stats getDurationStats() {
        return durationStats;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setEffectName(String effectName) {
        this.effectName = effectName;
    }

    public void setEffectDescription(String effectDescription) {
        this.effectDescription = effectDescription;
    }

    public void setEffectApplyMessages(List<String> effectApplyMessages) {
        this.effectApplyMessages = effectApplyMessages;
    }
}
