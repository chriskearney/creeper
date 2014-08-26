package com.comandante.creeper.Items;

import java.util.UUID;

import static com.comandante.creeper.model.Color.RED;
import static com.comandante.creeper.model.Color.RESET;
import static com.comandante.creeper.model.Color.YELLOW;

public enum ItemType {
    UNKNOWN(0, "", "", "", false, 0, 0),
    KEY(1, "key", new StringBuilder()
            .append(YELLOW)
            .append("    a shiny gold key")
            .append(RESET).toString(),
            "A basic key with nothing really remarkable other than its made of gold.",
            false,
            0,
            2),
    BOOK(2, "book", new StringBuilder()
            .append(RED)
            .append("    an ancient leather bound book")
            .append(RESET).toString(),
            "An ancient book with mysterious engravings.",
            false,
            0,
            2),
    BEER(3, "beer", new StringBuilder()
            .append(YELLOW)
            .append("    a cold frosty beer")
            .append(RESET).toString(),
            "A coors light.",
            true,
            2,
            2);

    private final Integer itemTypeCode;
    private final String itemShortName;
    private final String itemName;
    private final String itemDescription;
    private final boolean isDisposable;
    private final int maxUses;
    private final int itemHalfLifeTicks;

    ItemType(Integer itemTypeCode, String itemShortName, String itemName, String itemDescription, boolean isDisposable, int maxUses, int itemHalfLifeTicks) {
        this.itemTypeCode = itemTypeCode;
        this.itemShortName = itemShortName;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.maxUses = maxUses;
        if (maxUses > 0) {
            this.isDisposable = true;
        } else {
            this.isDisposable = isDisposable;
        }
        this.itemHalfLifeTicks = itemHalfLifeTicks;
    }

    public Item create() {
        return new Item(getItemName(), getItemDescription(), getItemShortName(), UUID.randomUUID().toString(), getItemTypeCode(), 0, false);
    }

    public Integer getItemTypeCode() {
        return itemTypeCode;
    }

    public String getItemShortName() {
        return itemShortName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public boolean isDisposable() {
        return isDisposable;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public int getItemHalfLifeTicks() {
        return itemHalfLifeTicks;
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
