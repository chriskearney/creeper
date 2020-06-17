package com.comandante.creeper.items;


import com.google.common.collect.Lists;

import java.util.List;

public enum EquipmentSlotType {

    HAND("hand"),
    HEAD("head"),
    FEET("feet"),
    LEGS("legs"),
    WRISTS("wrists"),
    CHEST("chest"),
    BAG("bag");

    private final String name;

    EquipmentSlotType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static EquipmentSlotType getByName(String name) {
        EquipmentSlotType[] values = EquipmentSlotType.values();
        for (EquipmentSlotType e: values) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }

    public static List<EquipmentSlotType> getAll() {
        return Lists.newArrayList(EquipmentSlotType.values());
    }
}
