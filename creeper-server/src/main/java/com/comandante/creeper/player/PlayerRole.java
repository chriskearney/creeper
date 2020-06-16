package com.comandante.creeper.player;

public enum PlayerRole {

    ADMIN("administrator", 1),
    MORTAL("mortal", 2),
    TELEPORTER("teleporter", 3),
    GOD("god", 3);
    
    private final String roleType;
    private final int roleId;

    PlayerRole(String roleType, int roleId) {
        this.roleType = roleType;
        this.roleId = roleId;
    }

    public String getRoleType() {
        return roleType;
    }

    public int getRoleId() {
        return roleId;
    }

    public static PlayerRole getByType(String type) {
        PlayerRole[] values = PlayerRole.values();
        for (PlayerRole playerRole : values) {
            if(playerRole.getRoleType().equals(type)) {
                return playerRole;
            }
        }
        return null;
    }
}
