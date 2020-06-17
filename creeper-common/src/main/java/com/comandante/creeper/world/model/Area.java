package com.comandante.creeper.world.model;

public enum Area {
    LOBBY("lobby"),
    NEWBIE_ZONE("newbie_zone"),
    HOUSE_ZONE("house_zone"),
    FANCYHOUSE_ZONE("fancyhouse_zone"),
    NORTH1_ZONE("north1_zone"),
    NORTH2_ZONE("north2_zone"),
    NORTH3_ZONE("north3_zone"),
    NORTH4_ZONE("north4_zone"),
    NORTH5_ZONE("north5_zone"),
    NORTH6_ZONE("north6_zone"),
    NORTH7_ZONE("north7_zone"),
    NORTH8_ZONE("north8_zone"),
    NORTH9_ZONE("north9_zone"),
    NORTH10_ZONE("north10_zone"),
    NORTH11_ZONE("north11_zone"),
    NORTH12_ZONE("north12_zone"),
    NORTH13_ZONE("north13_zone"),
    NORTH14_ZONE("north14_zone"),
    NORTH15_ZONE("north15_zone"),
    BLOODRIDGE1_ZONE("bloodridge1_zone"),
    BLOODRIDGE2_ZONE("bloodridge2_zone"),
    BLOODRIDGE3_ZONE("bloodridge3_zone"),
    BLOODRIDGE4_ZONE("bloodridge4_zone"),
    BLOODRIDGE5_ZONE("bloodridge5_zone"),
    BLOODRIDGE6_ZONE("bloodridge6_zone"),
    BLOODRIDGE7_ZONE("bloodridge7_zone"),
    BLOODRIDGE8_ZONE("bloodridge8_zone"),
    BLOODRIDGE9_ZONE("bloodridge9_zone"),
    BLOODRIDGE10_ZONE("bloodridge10_zone"),
    BLOODRIDGE11_ZONE("bloodridge11_zone"),
    BLOODRIDGE12_ZONE("bloodridge12_zone"),
    BLOODRIDGE13_ZONE("bloodridge13_zone"),
    BLOODRIDGE14_ZONE("bloodridge14_zone"),
    BLOODRIDGE15_ZONE("bloodridge15_zone"),
    BLOODRIDGE16_ZONE("bloodridge16_zone"),
    BLOODRIDGE17_ZONE("bloodridge17_zone"),
    BLOODRIDGE18_ZONE("bloodridge18_zone"),
    BLOODRIDGE19_ZONE("bloodridge19_zone"),
    BLOODRIDGE20_ZONE("bloodridge20_zone"),
    WESTERN1_ZONE("western1_zone"),
    WESTERN2_ZONE("western2_zone"),
    WESTERN3_ZONE("western3_zone"),
    WESTERN4_ZONE("western4_zone"),
    WESTERN5_ZONE("western5_zone"),
    WESTERN6_ZONE("western6_zone"),
    WESTERN7_ZONE("western7_zone"),
    WESTERN8_ZONE("western8_zone"),
    WESTERN9_ZONE("western9_zone"),
    WESTERN10_ZONE("western10_zone"),
    TOFT1_ZONE("toft1_zone"),
    TOFT2_ZONE("toft2_zone"),
    TOFT3_ZONE("toft3_zone"),
    TOFT4_ZONE("toft4_zone"),
    TOFT5_ZONE("toft5_zone"),
    TISLAND1_ZONE("tisland1_zone"),
    TISLAND2_ZONE("tisland2_zone"),
    TISLAND3_ZONE("tisland3_zone"),
    TISLAND4_ZONE("tisland4_zone"),
    TISLAND5_ZONE("tisland5_zone"),
    TISLAND6_ZONE("tisland6_zone"),
    TISLAND7_ZONE("tisland7_zone"),
    TISLAND8_ZONE("tisland8_zone"),
    TISLAND9_ZONE("tisland9_zone"),
    TISLAND10_ZONE("tisland10_zone"),
    SOUTH1_ZONE("south1_zone"),
    SOUTH2_ZONE("south2_zone"),
    SOUTH3_ZONE("south3_zone"),
    SOUTH4_ZONE("south4_zone"),
    SOUTH5_ZONE("south5_zone"),
    SOUTH6_ZONE("south6_zone"),
    SOUTH7_ZONE("south7_zone"),
    SOUTH8_ZONE("south8_zone"),
    SOUTH9_ZONE("south9_zone"),
    SOUTH10_ZONE("south10_zone"),
    RADIO_ROOM("radio_room");

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
