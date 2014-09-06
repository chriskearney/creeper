package com.comandante.creeper.world;

public enum Area {
    DEFAULT("default"),
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
