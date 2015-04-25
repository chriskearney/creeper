package com.comandante.creeper.npc;

import com.comandante.creeper.Items.Loot;
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

public class RedEyedBear extends Npc {
    private final static long phraseIntervalMs = 300000;
    private final static String NAME = "red-eyed bear";
    private final static Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"r", "red-eyed", "bear","b", NAME}
    ));

    private final static String colorName = "red-eyed" + BOLD_ON + Color.MAGENTA + " bear"  + Color.RESET ;
    private final static String dieMessage = "a " + colorName + " breathes his last breath in a pool of " + BOLD_ON + Color.RED + "blood" + RESET + ".";

    public RedEyedBear(GameManager gameManager, Loot loot) {
        super(gameManager, NAME, colorName, 0, NpcStats.REDEYED_BEAR.createStats(), dieMessage, Optional.<HashSet<Area>>absent(), validTriggers, loot);
    }

    @Override
    public RedEyedBear create(GameManager gameManager, Loot loot) {
        return new RedEyedBear(gameManager, loot);
    }
}