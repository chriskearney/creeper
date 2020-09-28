package com.comandante.creeper.events;

public class PlayerUpdateHealthEvent {

    private String npcId;
    private long amount;
    private String playerName;

    public PlayerUpdateHealthEvent(String npcId, long amount, String playerName) {
        this.npcId = npcId;
        this.amount = amount;
        this.playerName = playerName;
    }

    public PlayerUpdateHealthEvent() {
    }

    public String getNpcId() {
        return npcId;
    }

    public long getAmount() {
        return amount;
    }

    public String getPlayerName() {
        return playerName;
    }
}
