package com.comandante.creeper.merchant;

import com.comandante.creeper.player.Quest;

import java.util.List;
import java.util.Set;

public class MerchantMetadata {

    private String internalName;
    private Set<Integer> roomIds;
    private String name;
    private String colorName;
    private Set<String> validTriggers;
    private List<MerchantItemForSale> merchantItemForSales;
    private String welcomeMessage;
    private Merchant.MerchantType merchantType;
    private List<Quest> quests;

    public void setMerchantType(Merchant.MerchantType merchantType) {
        this.merchantType = merchantType;
    }

    public Set<Integer> getRoomIds() {

        return roomIds;
    }

    public Merchant.MerchantType getMerchantType() {
        return merchantType;
    }

    public String getInternalName() {
        return internalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public Set<String> getValidTriggers() {
        return validTriggers;
    }

    public void setValidTriggers(Set<String> validTriggers) {
        this.validTriggers = validTriggers;
    }

    public List<MerchantItemForSale> getMerchantItemForSales() {
        return merchantItemForSales;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public Set<Integer> getRoomId() {
        return roomIds;
    }

    public void setRoomIds(Set<Integer> roomIds) {
        this.roomIds = roomIds;
    }

    public void setMerchantItemForSales(List<MerchantItemForSale> merchantItemForSales) {
        this.merchantItemForSales = merchantItemForSales;

    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void setQuests(List<Quest> quests) {
        this.quests = quests;
    }
}
