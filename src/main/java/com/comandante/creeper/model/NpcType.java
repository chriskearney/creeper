package com.comandante.creeper.model;

import org.fusesource.jansi.Ansi;

import java.util.Arrays;
import java.util.List;


public enum NpcType {

    DERPER(Arrays.asList(
            "Zug zug.",
            "Don't provoke me."), 300000, new StringBuilder().append(new Ansi().fg(Ansi.Color.RED).toString()).append("derper").append(new Ansi().reset().toString()).toString());

    private final List<String> phrases;
    private final long phrasesIntervalMs;
    private final String npcName;

    NpcType(List<String> phrases, long phrasesIntervalMs, String npcName) {
        this.phrases = phrases;
        this.phrasesIntervalMs = phrasesIntervalMs;
        this.npcName = npcName;
    }

    public List<String> getPhrases() {
        return phrases;
    }

    public long getPhrasesIntervalMs() {
        return phrasesIntervalMs;
    }

    public String getNpcName() {
        return npcName;
    }
}
