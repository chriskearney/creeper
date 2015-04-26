package com.comandante.creeper.player;

public enum PlayerRoles {

    ADMIN("administrator", 1);

    private final String roleType;
    private final int roleId;

    PlayerRoles(String roleType, int roleId) {
        this.roleType = roleType;
        this.roleId = roleId;
    }
}
