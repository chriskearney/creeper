package com.comadante.creeper.cclient;

import java.util.List;

public class ConsoleStatusBarDetails {
    private String playerName;
    private Long healthAmount;
    private Long totalHealthAmount;
    private Long manaAmount;
    private Long totalManaamount;
    private List<String> activeCoolDowns;
    private Long goldAmount;
    private Long bankAccountAmount;
    private Boolean isFight;
    private Long level;
    private Long xp;
    private Long xpToNextLevel;
    private Boolean isDead;
    private String currentAreas;

    public ConsoleStatusBarDetails() {
    }

    public ConsoleStatusBarDetails(String playerName, Long healthAmount, Long totalHealthAmount, Long manaAmount, Long totalManaamount, List<String> activeCoolDowns, Long goldAmount, Long bankAccountAmount, Boolean isFight, Long level, Long xp, Long xpToNextLevel, Boolean isDead, String currentAreas) {
        this.playerName = playerName;
        this.healthAmount = healthAmount;
        this.totalHealthAmount = totalHealthAmount;
        this.manaAmount = manaAmount;
        this.totalManaamount = totalManaamount;
        this.activeCoolDowns = activeCoolDowns;
        this.goldAmount = goldAmount;
        this.bankAccountAmount = bankAccountAmount;
        this.isFight = isFight;
        this.level = level;
        this.xp = xp;
        this.xpToNextLevel = xpToNextLevel;
        this.isDead = isDead;
        this.currentAreas = currentAreas;
    }

    public String getPlayerName() {
        return playerName;
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

    public Boolean getIsFight() {
        return isFight;
    }

    public Boolean getFight() {
        return isFight;
    }

    public Long getLevel() {
        return level;
    }

    public Long getXp() {
        return xp;
    }

    public Long getXpToNextLevel() {
        return xpToNextLevel;
    }

    public Boolean getDead() {
        return isDead;
    }

    public String getCurrentAreas() {
        return currentAreas;
    }
}