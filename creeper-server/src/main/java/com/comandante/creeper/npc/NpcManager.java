package com.comandante.creeper.npc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NpcManager {

    private final Map<String, Npc> npcs = new ConcurrentHashMap<>();

    public void addNpc(Npc npc) {
        npcs.put(npc.getEntityId(), npc);
    }

    public void deleteNpcEntity(String npcId) {
        npcs.remove(npcId);
    }

    public Npc getNpc(String npcId) {
        return npcs.get(npcId);
    }

    public Map<String, Npc> getNpcs() {
        return npcs;
    }
}
