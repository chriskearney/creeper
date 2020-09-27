package com.comandante.creeper.events;

public class DrawMapEvent {

    private final String map;

    public DrawMapEvent(String map) {
        this.map = map;
    }

    public String getMap() {
        return map;
    }
}
