package com.comandante.creeper.npc;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.world.Area;
import com.comandante.creeper.server.Color;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.comandante.creeper.server.Color.BOLD_ON;
import static com.comandante.creeper.server.Color.RESET;

public class SwampBerserker extends Npc {
    private final static long phraseIntervalMs = 300000;
    private final static String NAME = "swamp berserker";
    private final static Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"s", "swamp", "berserker","b", NAME}
    ));

    private final static String colorName = "swamp" + BOLD_ON + Color.MAGENTA + " berserker"  + Color.RESET ;
    private final static String dieMessage = "a " + colorName + " breathes his last breath in a pool of " + BOLD_ON + Color.RED + "blood" + RESET + ".";

    public SwampBerserker(GameManager gameManager) {
        super(gameManager, NAME, colorName, 0, NpcStats.SWAMP_BERSERKER.createStats(), dieMessage, Optional.of(Sets.newHashSet(Area.NORTH2_ZONE)), validTriggers);
    }

    @Override
    public SwampBerserker create(GameManager gameManager) {
        return new SwampBerserker(gameManager);
    }
}