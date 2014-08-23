package com.comandante.creeper.model.npc;


import java.util.UUID;

public class Npc {

    private final String npcId;
    private final NpcType npcType;
    private long phraseTimestamp;

    public Npc(NpcType npcType) {
        npcId = UUID.randomUUID().toString();
        this.npcType = npcType;
        this.phraseTimestamp = 0;
    }

    public String getNpcId() {
        return npcId;
    }

    public NpcType getNpcType() {
        return npcType;
    }

    public long getPhraseTimestamp() {
        return phraseTimestamp;
    }

    public void setPhraseTimestamp(long phraseTimestamp) {
        this.phraseTimestamp = phraseTimestamp;
    }
}
