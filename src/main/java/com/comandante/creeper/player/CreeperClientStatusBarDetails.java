package com.comandante.creeper.player;

import java.util.List;

public class CreeperClientStatusBarDetails {
    private final Long healthAmount;
    private final Long totalHealthAmount;
    private final Long manaAmount;
    private final Long totalManaamount;
    private final List<String> activeCoolDowns;
    private final Long goldAmount;
    private final Long bankAccountAmount;
    private final Boolean isFight;

    public CreeperClientStatusBarDetails(Long healthAmount, Long totalHealthAmount, Long manaAmount, Long totalManaamount, List<String> activeCoolDowns, Long goldAmount, Long bankAccountAmount, Boolean isFight) {
        this.healthAmount = healthAmount;
        this.totalHealthAmount = totalHealthAmount;
        this.manaAmount = manaAmount;
        this.totalManaamount = totalManaamount;
        this.activeCoolDowns = activeCoolDowns;
        this.goldAmount = goldAmount;
        this.bankAccountAmount = bankAccountAmount;
        this.isFight = isFight;

    }

    public Long getHealthAmount() {
        return healthAmount;
    }

    public Long getTotalHealthAmount() {
        return totalHealthAmount;
    }

    public Long getManaAmount() {
        return manaAmount;
    }

    public Long getTotalManaamount() {
        return totalManaamount;
    }

    public java.util.List<String> getActiveCoolDowns() {
        return activeCoolDowns;
    }

    public Long getGoldAmount() {
        return goldAmount;
    }

    public Long getBankAccountAmount() {
        return bankAccountAmount;
    }

    public Boolean getFight() {
        return isFight;
    }

}
