package com.comandante.creeper.world;

public enum Area {
    LOBBY("lobby"),
    NEWBIE_ZONE("newbie_zone"),
    HOUSE_ZONE("house_zone"),
    NORTH1_ZONE("north1_zone"),
    NORTH2_ZONE("north2_zone"),
    NORTH3_ZONE("north3_zone"),
    NORTH4_ZONE("north4_zone"),
    BLOODRIDGE1_ZONE("bloodridge1_zone"),
    BLOODRIDGE2_ZONE("bloodridge2_zone"),
    BLOODRIDGE3_ZONE("bloodridge3_zone"),
    BLOODRIDGE4_ZONE("bloodridge4_zone"),
    WESTERN1_ZONE("western1_zone"),
    WESTERN2_ZONE("western2_zone"),
    WESTERN3_ZONE("western3_zone"),
    WESTERN4_ZONE("western4_zone"),
    WESTERN5_ZONE("western5_zone"),
    TOFT1_ZONE("toft1_zone"),
    TOFT2_ZONE("toft2_zone"),
    TOFT3_ZONE("toft3_zone"),
    TISLAND1_ZONE("tisland1_zone"),
    TISLAND2_ZONE("tisland2_zone"),
    TISLAND3_ZONE("tisland3_zone");

    private final String name;

    Area(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Area getByName(String name) {
        Area[] values = Area.values();
        for (Area area : values) {
            if (area.getName().equals(name)) {
                return area;
            }
        }
        return null;
    }
}
