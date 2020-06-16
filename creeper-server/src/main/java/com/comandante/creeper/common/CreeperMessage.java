package com.comandante.creeper.common;


public class CreeperMessage {

    private final Type type;
    private final String message;

    public CreeperMessage(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public enum Type {
        NORMAL,
        CRITICAL
    }
}
