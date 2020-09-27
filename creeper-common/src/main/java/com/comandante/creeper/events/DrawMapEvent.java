package com.comandante.creeper.events;

public class DrawMapEvent {

    private String map;

    public DrawMapEvent() {
    }

    public DrawMapEvent(String map) {
        this.map = map;
    }

    public String getMap() {
        return map;
    }
}
