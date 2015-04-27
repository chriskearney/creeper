package com.comandante.creeper.npc;


import com.comandante.creeper.Items.Loot;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.server.Color;
import com.comandante.creeper.world.Area;
import com.google.common.base.Optional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.comandante.creeper.server.Color.BOLD_ON;
import static com.comandante.creeper.server.Color.RESET;

public class GrayEkimmu extends Npc {
    private final static long phraseIntervalMs = 300000;
    private final static String NAME = "gray ekimmu";
    private final static Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"g", "gray", "ekimmu","e", NAME}
    ));

    private final static String colorName = "gray" + BOLD_ON + Color.MAGENTA + " ekimmu"  + Color.RESET ;
    private final static String dieMessage = "a " + colorName + " breathes his last breath in a pool of " + BOLD_ON + Color.RED + "blood" + RESET + ".";

    public GrayEkimmu(GameManager gameManager, Loot loot) {
        super(gameManager, NAME, colorName, 0, NpcStats.GRAY_EKIMMU.createStats(), dieMessage, Optional.<HashSet<Area>>absent(), validTriggers, loot);
    }

    @Override
    public GrayEkimmu create(GameManager gameManager, Loot loot) {
        return new GrayEkimmu(gameManager, loot);
    }
}