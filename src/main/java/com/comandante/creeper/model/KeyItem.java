package com.comandante.creeper.model;


import com.comandante.creeper.managers.GameManager;
import org.fusesource.jansi.Ansi;

public class KeyItem extends Item {

    private final static String NAME = new StringBuilder()
            .append(new Ansi().fg(Ansi.Color.YELLOW).toString())
            .append("A shiny gold key")
            .append(new Ansi().reset().toString()).toString();
    private final static String DESCRIPTION = "It's a freaking key man.";

    public KeyItem(GameManager gameManager) {
        super(gameManager, NAME, DESCRIPTION);
    }

    @Override
    public void run() {

    }
}
