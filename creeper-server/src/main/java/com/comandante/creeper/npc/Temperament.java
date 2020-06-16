package com.comandante.creeper.npc;

public enum Temperament {
    AGGRESSIVE("Aggressive"),
    PASSIVE("Passive");

    private final String friendlyFormat;

    Temperament(String friendlyFormat) {
        this.friendlyFormat = friendlyFormat;
    }

    public String getFriendlyFormat() {
        return friendlyFormat;
    }

    public static Temperament get(String s) {
        for (Temperament t : Temperament.values()) {
            if (t.name().equalsIgnoreCase(s)) {
                return t;
            }
        }
        return null;
    }
}
