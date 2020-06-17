package com.comandante.creeper.items;

import com.comandante.creeper.stats.Stats;

import java.util.List;

public class EffectBuilder {
    private String effectName;
    private String effectDescription;
    private List<String> effectApplyMessages;
    private Stats applyStatsOnTick;
    private Stats durationStats;
    private int lifeSpanTicks;
    private boolean frozenMovement;

    public EffectBuilder setEffectName(String effectName) {
        this.effectName = effectName;
        return this;
    }

    public EffectBuilder setEffectDescription(String effectDescription) {
        this.effectDescription = effectDescription;
        return this;
    }

    public EffectBuilder setEffectApplyMessages(List<String> effectApplyMessages) {
        this.effectApplyMessages = effectApplyMessages;
        return this;
    }

    public EffectBuilder setApplyStatsOnTick(Stats applyStatsOnTick) {
        this.applyStatsOnTick = applyStatsOnTick;
        return this;
    }

    public EffectBuilder setDurationStats(Stats durationStats) {
        this.durationStats = durationStats;
        return this;
    }

    public EffectBuilder setLifeSpanTicks(int lifeSpanTicks) {
        this.lifeSpanTicks = lifeSpanTicks;
        return this;
    }

    public EffectBuilder setFrozenMovement(boolean frozenMovement) {
        this.frozenMovement = frozenMovement;
        return this;
    }

    public Effect createEffect() {
        return new Effect(effectName, effectDescription, effectApplyMessages, applyStatsOnTick, durationStats, lifeSpanTicks, frozenMovement);
    }
}