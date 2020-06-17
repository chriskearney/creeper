package com.comandante.creeper.player;

import java.lang.reflect.Type;

public enum PlayerSettings {
    AUTO_MAP("auto_map", "Automatically draws the creeper map on the CurrentRoomLogic function.", Integer.TYPE);

    private final String settingName;
    private final String settingDescription;
    private final Type type;

    PlayerSettings(String settingName, String settingDescription, Type type) {
        this.settingName = settingName;
        this.settingDescription = settingDescription;
        this.type = type;
    }

    public static PlayerSettings getByKey(String key) {
        PlayerSettings[] values = values();
        for (PlayerSettings playerSettings: values) {
            if (playerSettings.settingName.equals(key)) {
                return playerSettings;
            }
        }
        return null;
    }

    public String getSettingName() {
        return settingName;
    }

    public String getSettingDescription() {
        return settingDescription;
    }

    public Type getType() {
        return type;
    }
}
