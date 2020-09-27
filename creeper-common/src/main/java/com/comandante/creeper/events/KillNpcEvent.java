package com.comandante.creeper.events;

public class KillNpcEvent {

    private String playerId;
    private String npcId;
    private long xpEarned;
    private String name;

    public KillNpcEvent() {
    }

    public KillNpcEvent(String playerId, String npcId, long xpEarned, String name) {
        this.playerId = playerId;
        this.npcId = npcId;
        this.xpEarned = xpEarned;
        this.name = name;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getNpcId() {
        return npcId;
    }

    public long getXpEarned() {
        return xpEarned;
    }

    public String getName() {
        return name;
    }
}
