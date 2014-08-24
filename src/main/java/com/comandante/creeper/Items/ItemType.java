package com.comandante.creeper.Items;

import org.fusesource.jansi.Ansi;

import java.util.UUID;

public enum ItemType {
    UNKNOWN(0, "", "", "", false, 0),
    KEY(1, "key", new StringBuilder()
            .append(new Ansi().fg(Ansi.Color.YELLOW).toString())
            .append("A shiny gold [key]")
            .append(new Ansi().reset().toString()).toString(),
            "A basic key with nothing really remarkable other than its made of gold.",
            false,
            0),
    BOOK(2, "book", new StringBuilder()
            .append(new Ansi().fg(Ansi.Color.RED).toString())
            .append("An ancient leather bound [book]")
            .append(new Ansi().reset().toString()).toString(),
            "An ancient book with mysterious engravings.",
            false,
            0),
    BEER(3, "beer", new StringBuilder()
            .append(new Ansi().fg(Ansi.Color.YELLOW).toString())
            .append("A cold frosty [beer]")
            .append(new Ansi().reset().toString()).toString(),
            "A coors light.",
            true,
            2);

    private final Integer itemTypeCode;
    private final String itemShortName;
    private final String itemName;
    private final String itemDescription;
    private final boolean isDisposable;
    private final int maxUses;

    ItemType(Integer itemTypeCode, String itemShortName, String itemName, String itemDescription, boolean isDisposable, int maxUses) {
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
    }

    public Item create() {
        return new Item(getItemName(), getItemDescription(), getItemShortName(), UUID.randomUUID().toString(), getItemTypeCode(), 0);
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
