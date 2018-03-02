package com.comandante.creeper.player;


public enum CoolDownType {

    DEATH("death", 150),
    FORAGE_LONG("forage-long", 7),
    FORAGE_MEDIUM("forage-medium", 4),
    FORAGE_SHORT("forage-short", 3),
    FORAGE_SUPERSHORT("forage-supershort", 1),
    SPELL("",0),
    NPC_FIGHT("fight",30),
    NPC_ROAM("npc-roam", 1200),
    NPC_ALERTED("npc-alerted", 30),
    PLAYER_RECALL("recall", 600),
    DETAINMENT("detained", 32768),
    NEWBIE("newbie", 20000),
    FIRE_SAUCE("fire-sauce", 1550);
    private final String name;
    private final int ticks;

    CoolDownType(String name, int ticks) {
        this.name = name;
        this.ticks = ticks;
    }

    public String getName() {
        return name;
    }

    public int getTicks() {
        return ticks;
    }
}
