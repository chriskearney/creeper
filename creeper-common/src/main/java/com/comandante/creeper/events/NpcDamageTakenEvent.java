package com.comandante.creeper.events;

public class NpcDamageTakenEvent {

    private String playerId;
    private String npcId;
    private long damageAmount;
    private String colorName;


    public NpcDamageTakenEvent() {
    }

    public NpcDamageTakenEvent(String playerId, String npcId, long damageAmount, String colorName) {
        this.playerId = playerId;
        this.npcId = npcId;
        this.damageAmount = damageAmount;
        this.colorName = colorName;
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

    public String getColorName() {
        return colorName;
    }
}
