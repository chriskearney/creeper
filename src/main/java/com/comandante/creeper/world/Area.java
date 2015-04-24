package com.comandante.creeper.world;

public enum Area {
    DEFAULT("default"),
    HOUSE_ZONE("house_zone"),
    NORTH1_ZONE("north1_zone"),
    NORTH2_ZONE("north2_zone"),
    BLOODRIDGE1_ZONE("bloodridge1_zone"),
    BLOODRIDGE2_ZONE("bloodridge2_zone"),
    WESTERN1_ZONE("western1_zone"),
    WESTERN2_ZONE("western2_zone"),
    NEWBIE_ZONE("newbie_zone");

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
        return DEFAULT;
    }
}
