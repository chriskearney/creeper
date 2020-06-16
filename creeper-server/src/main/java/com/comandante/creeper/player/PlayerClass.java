package com.comandante.creeper.player;

public enum PlayerClass {

    WARRIOR(1, "warrior", "A warrior is skilled in mele combat and feats of strength."),
    WIZARD(2, "wizard", "A wizard is a master of the deadly side of the arcane arts with a high IQ."),
    RANGER(3, "ranger", "A ranger moves quickly and is deadly from a distance"),
    SHAMAN(4, "shaman", "A shaman possesses a mastery of the restoratitve arcane arts."),
    BASIC(0, "basic", "A master of nothing.");

    private final int id;
    private final String identifier;
    private final String description;

    PlayerClass(int id, String identifier, String description) {
        this.id = id;
        this.identifier = identifier;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }

    public DamageProcessor getDamageProcessor() {
        switch (this) {
            case WARRIOR:
            case WIZARD:
            case RANGER:
            case SHAMAN:
            case BASIC:
                return new BasicPlayerDamageProcessor();
            default:
                return new BasicPlayerDamageProcessor();
        }
    }
}
