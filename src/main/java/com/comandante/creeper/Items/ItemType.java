package com.comandante.creeper.Items;

public enum ItemType {
    UNKNOWN(0),
    KEY(1);

    private final Integer itemTypeCode;

    ItemType(Integer itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    public Integer getItemTypeCode() {
        return itemTypeCode;
    }

    public static ItemType itemTypeFromCode(Integer code) {
        ItemType[] values = values();
        for (ItemType type : values) {
            if (type.getItemTypeCode().equals(code)) {
                return type;
            }
        }
        return ItemType.UNKNOWN;
    }
}
