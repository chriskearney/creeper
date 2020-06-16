package com.comandante.creeper.stats.modifier;

import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Stats;

public interface StatsModifier {

    public Stats modify(Player player);

}
