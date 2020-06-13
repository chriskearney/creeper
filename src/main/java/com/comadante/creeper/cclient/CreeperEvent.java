package com.comadante.creeper.cclient;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class CreeperEvent {

    private UUID uuid;
    private CreeperEventType creeperEventType;
    private String payload;
    private Long epochTimestamp;
    private Optional<String> playerId;
    private Audience audience;

    public CreeperEvent() {
    }

    private CreeperEvent(UUID uuid, CreeperEventType creeperEventType, String payload, Long epochTimestamp, Optional<String> playerId, Audience audience) {
        this.uuid = uuid;
        this.creeperEventType = creeperEventType;
        this.payload = payload;
        this.epochTimestamp = epochTimestamp;
        this.playerId = playerId;
        this.audience = audience;
    }

    public UUID getUuid() {
        return uuid;
    }

    public CreeperEventType getCreeperEventType() {
        return creeperEventType;
    }

    public String getPayload() {
        return payload;
    }

    public Long getEpochTimestamp() {
        return epochTimestamp;
    }

    public Optional<String> getPlayerId() {
        return playerId;
    }

    public Audience getAudience() {
        return audience;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreeperEvent that = (CreeperEvent) o;
        return Objects.equals(uuid, that.uuid) &&
                creeperEventType == that.creeperEventType &&
                Objects.equals(payload, that.payload) &&
                Objects.equals(epochTimestamp, that.epochTimestamp) &&
                Objects.equals(playerId, that.playerId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid, creeperEventType, payload, epochTimestamp, playerId);
    }

    public static class Builder {
        private UUID uuid = UUID.randomUUID();
        private CreeperEventType creeperEventType;
        private String payload;
        private Long epochTimestamp = System.currentTimeMillis();
        private Optional<String> playerId = Optional.empty();
        private Audience audience;

        public Builder creeperEventType(CreeperEventType creeperEventType) {
            this.creeperEventType = creeperEventType;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder epochTimestamp(Long epochTimestamp) {
            this.epochTimestamp = epochTimestamp;
            return this;
        }

        public Builder playerId(String playerId) {
            this.playerId = Optional.ofNullable(playerId);
            return this;
        }

        public Builder audience(Audience audience) {
            this.audience = audience;
            return this;
        }

        public CreeperEvent build() {
            return new CreeperEvent(uuid, creeperEventType, payload, epochTimestamp, playerId, audience);
        }
    }

    public enum Audience {
        EVERYONE,
        PLAYER_ONLY
    }

}
