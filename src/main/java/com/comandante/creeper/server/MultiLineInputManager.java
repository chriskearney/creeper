package com.comandante.creeper.server;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class MultiLineInputManager {

    private final Map<UUID, StringBuilder> multiLineInputs = Maps.newConcurrentMap();

    public void addToMultiLine(UUID uuid, String input) {
        multiLineInputs.get(uuid).append(input).append("\r\n");
    }

    public String retrieveMultiLineInput(UUID uuid) {
        return  multiLineInputs.remove(uuid).toString();
    }

    public UUID createNewMultiLineInput() {
        UUID retrievalId = UUID.randomUUID();
        multiLineInputs.put(retrievalId, new StringBuilder());
        return retrievalId;
    }
}
