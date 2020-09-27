package com.comandante.creeper.events;

public class KillNpcEvent {

    private final String playerId;
    private final String npcId;
    private final long xpEarned;
    private final String name;

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
