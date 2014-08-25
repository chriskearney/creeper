package com.comandante.creeper.model;

import org.fusesource.jansi.Ansi;

public final class Color {
    public static final String YELLOW = new Ansi().fg(Ansi.Color.YELLOW).toString();
    public static final String DEFAULT = new Ansi().fg(Ansi.Color.DEFAULT).toString();
    public static final String WHITE = new Ansi().fg(Ansi.Color.WHITE).toString();
    public static final String GREEN = new Ansi().fg(Ansi.Color.GREEN).toString();
    public static final String CYAN = new Ansi().fg(Ansi.Color.CYAN).toString();
    public static final String BLACK = new Ansi().fg(Ansi.Color.BLACK).toString();
    public static final String BLUE = new Ansi().fg(Ansi.Color.BLUE).toString();
    public static final String MAGENTA = new Ansi().fg(Ansi.Color.MAGENTA).toString();
    public static final String RED = new Ansi().fg(Ansi.Color.RED).toString();
    public static final String RESET = new Ansi().reset().toString();
}
