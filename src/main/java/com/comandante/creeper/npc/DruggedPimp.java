package com.comandante.creeper.npc;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.room.Area;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

import java.util.Random;

import static com.comandante.creeper.server.Color.GREEN;
import static com.comandante.creeper.server.Color.RESET;

public class DruggedPimp extends Npc {
    private final static long phraseIntervalMs = 300000;
    private final static String NAME = "drugged pimp";
    private final Random random;
    private final static String colorName = new StringBuilder()
            .append(GREEN)
            .append("drugged pimp")
            .append(RESET).toString();

    public DruggedPimp(GameManager gameManager) {
        super(gameManager, NAME, colorName, 0, NpcStats.DRUGGED_PIMP.createStats(), "a drugged pimp is dead and broke", Optional.of(Sets.newHashSet(Area.NEWBIE_ZONE)));
        this.random = new Random();
    }

    @Override
    public DruggedPimp create(GameManager gameManager) {
        return new DruggedPimp(gameManager);
    }
}
