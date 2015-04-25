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

public class BergOrc extends Npc {
    private final static long phraseIntervalMs = 300000;
    private final static String NAME = "berg orc";
    private final static Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"b", "berg", "orc", "o", NAME}
    ));

    private final static String colorName = "berg" + BOLD_ON + Color.GREEN + " orc" + Color.RESET;
    private final static String dieMessage = "a " + colorName + " breathes his last breath in a pool of " + BOLD_ON + Color.RED + "blood" + RESET + ".";

    public BergOrc(GameManager gameManager, Loot loot) {
        super(gameManager, NAME, colorName, 0, NpcStats.BERG_ORC.createStats(), dieMessage, Optional.of(Sets.newHashSet(Area.BLOODRIDGE1_ZONE)), validTriggers, loot);
    }

    @Override
    public BergOrc create(GameManager gameManager, Loot loot) {
        return new BergOrc(gameManager, loot);
    }
}