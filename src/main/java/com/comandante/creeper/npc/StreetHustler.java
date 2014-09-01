package com.comandante.creeper.npc;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.room.Area;
import com.comandante.creeper.server.Color;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.comandante.creeper.server.Color.BRIGHT_RED;
import static com.comandante.creeper.server.Color.RESET;

public class StreetHustler extends Npc {
    private final static long phraseIntervalMs = 300000;
    private final static String NAME = "street hustler";
    private final static Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
            {"s", "street", "hustler", NAME}
    ));

    private final static String colorName = Color.BOLD_ON + Color.BRIGHT_MAGENTA + Color.BOLD_OFF + "s" + Color.YELLOW + "t" + Color.BLUE + "r" + Color.MAGENTA + "e" + Color.BRIGHT_WHITE + "e" + Color.RED + "t" + Color.BRIGHT_GREEN + " " +
            Color.BRIGHT_YELLOW + "hustler" + RESET;
    private final static String dieMessage = "a " + colorName + " breathes his last breath in a pool of " + BRIGHT_RED + "blood" + RESET;

    public StreetHustler(GameManager gameManager) {
        super(gameManager, NAME, colorName, 0, NpcStats.DRUGGED_PIMP.createStats(), dieMessage, Optional.of(Sets.newHashSet(Area.NEWBIE_ZONE)), validTriggers);
    }

    @Override
    public StreetHustler create(GameManager gameManager) {
        return new StreetHustler(gameManager);
    }
}
