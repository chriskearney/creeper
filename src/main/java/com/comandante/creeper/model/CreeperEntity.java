package com.comandante.creeper.model;

import java.util.UUID;

public abstract class CreeperEntity implements Runnable {

    private final String entityId;

    protected CreeperEntity() {
        this.entityId = UUID.randomUUID().toString();
    }

    protected CreeperEntity(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }
}
