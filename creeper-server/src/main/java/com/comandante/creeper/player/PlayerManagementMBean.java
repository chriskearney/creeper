package com.comandante.creeper.player;

import java.util.Map;

public interface PlayerManagementMBean {

    void setMarkForDelete(boolean isMark);

    boolean getMarkForDelete();

    long getGold();

    long getGoldInBankAmount();

    void setGoldInBankAmount(long amt);

    void setGold(long amt);

    long getHealth();

    long getMana();

    void setHealth(long amt);

    void setMana(long amt);

    void sendAdminMessage(String message);

    void setExperience(long amt);

    long getExperience();

    void setRoles(String roles);

    String getRoles();

    Map<String, String> getInventory();

    String getPassword();

    void setPassword(String password);

    Map<String, String> getLockerInventory();

    String createItemInInventory(String internalItemName);

    void setPlayerClass(String playerClassName);

    String getPlayerClass();

    void clearAllCoolDowns();

    void detain();
}
