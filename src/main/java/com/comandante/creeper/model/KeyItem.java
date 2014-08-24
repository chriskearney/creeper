package com.comandante.creeper.model;


import org.fusesource.jansi.Ansi;

import java.util.UUID;

public class KeyItem extends Item {

    private final static String NAME = new StringBuilder()
            .append(new Ansi().fg(Ansi.Color.YELLOW).toString())
            .append("A shiny gold [key]")
            .append(new Ansi().reset().toString()).toString();
    private final static String SHORTNAME = "key";
    private final static String DESCRIPTION = "It's a freaking key man.";

    public KeyItem() {
        super(NAME, DESCRIPTION, SHORTNAME, UUID.randomUUID().toString());
    }
}
