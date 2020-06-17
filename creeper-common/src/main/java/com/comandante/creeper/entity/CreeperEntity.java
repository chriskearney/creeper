package com.comandante.creeper.entity;

import java.util.UUID;

public abstract class CreeperEntity implements Runnable {

    private String entityId;

    protected CreeperEntity() {
        this.entityId = UUID.randomUUID().toString();
    }

    protected CreeperEntity(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
}
