package com.comandante.creeper.core_game.service;

import com.comandante.creeper.server.player_communication.Color;

public enum TimeOfDay {
    MORNING(Color.YELLOW),
    AFTERNOON(Color.GREEN),
    EVENING(Color.RED),
    NIGHT(Color.CYAN);

    public String color;

    TimeOfDay(String color) {
        this.color = color;
    }
}
