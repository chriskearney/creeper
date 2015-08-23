package com.comandante.creeper.player;

import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.stat.Stats;

public interface StatsModifier {

    public Stats modify(Player player);

    public Stats modify(Npc npc);
}
