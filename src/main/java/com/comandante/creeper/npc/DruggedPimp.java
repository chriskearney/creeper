package com.comandante.creeper.npc;

import com.comandante.creeper.managers.GameManager;

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

    public DruggedPimp(GameManager gameManager, Integer roomId) {
        super(gameManager, roomId, NAME, colorName, 0, NpcStats.DRUGGED_PIMP.createStats(), "a drugged pimp is dead and broke");
        this.random = new Random();
    }

    @Override
    public DruggedPimp create(GameManager gameManager, Integer roomId) {
        return new DruggedPimp(gameManager, roomId);
    }
}
