package com.comandante.creeper.events;

public class NpcDamageTakenEvent {

    private String playerId;
    private String npcId;
    private long damageAmount;

    public NpcDamageTakenEvent() {
    }

    public NpcDamageTakenEvent(String playerId, String npcId, long damageAmount) {
        this.playerId = playerId;
        this.npcId = npcId;
        this.damageAmount = damageAmount;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getNpcId() {
        return npcId;
    }

    public long getDamageAmount() {
        return damageAmount;
    }
}
