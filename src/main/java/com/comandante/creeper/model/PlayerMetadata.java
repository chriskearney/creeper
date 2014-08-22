package com.comandante.creeper.model;


import java.io.Serializable;

public class PlayerMetadata implements Serializable {

    String playerName;
    String password;
    String playerId;
    int health;
    int stamina;
    int strength;
    int dexterity;

    public PlayerMetadata(String playerName, String password, String playerId) {
        this.playerName = playerName;
        this.password = password;
        this.playerId = playerId;
    }

    public String getPassword() {
        return password;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getHealth() {
        return health;
    }

    public int getStamina() {
        return stamina;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerMetadata that = (PlayerMetadata) o;

        if (dexterity != that.dexterity) return false;
        if (health != that.health) return false;
        if (stamina != that.stamina) return false;
        if (strength != that.strength) return false;
        if (!password.equals(that.password)) return false;
        if (!playerId.equals(that.playerId)) return false;
        if (!playerName.equals(that.playerName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = playerName.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + playerId.hashCode();
        result = 31 * result + health;
        result = 31 * result + stamina;
        result = 31 * result + strength;
        result = 31 * result + dexterity;
        return result;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

}
