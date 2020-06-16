package com.comandante.creeper.player;

import java.util.Optional;

public class ActiveFight {
    private final String npcId;
    private final String playerId;
    private boolean isPrimary;

    public static Builder builder() { return new Builder();}

    private ActiveFight(String npcId, String playerId, boolean isPrimary) {
        this.npcId = npcId;
        this.playerId = playerId;
        this.isPrimary = isPrimary;
    }

    public Optional<String> getNpcId() {
        return Optional.ofNullable(npcId);
    }

    public Optional<String> getPlayerId() {
        return Optional.ofNullable(playerId);
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public static class Builder {

        private String npcId;
        private String playerId;
        private boolean isPrimary;

        public Builder npcId(String npcId) {
            this.npcId = npcId;
            return this;
        }

        public Builder playerId(String playerId) {
            this.playerId = playerId;
            return this;
        }

        public Builder isPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public ActiveFight create() {
            return new ActiveFight(npcId, playerId, isPrimary);
        }

    }
}
