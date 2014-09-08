package com.comandante.creeper.server;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;
import java.util.UUID;

public class MultiLineInputManager {

    private final Map<UUID, StringBuilder> multiLineInputs = Maps.newConcurrentMap();

    public void addToMultiLine(UUID uuid, String input) {
        multiLineInputs.get(uuid).append(input);
    }

    public String retrieveMultiLineInput(UUID uuid) {
        return  removeTrailingBlankLines(multiLineInputs.remove(uuid).toString());
    }

    public UUID createNewMultiLineInput() {
        UUID retrievalId = UUID.randomUUID();
        multiLineInputs.put(retrievalId, new StringBuilder());
        return retrievalId;
    }

    private String removeTrailingBlankLines(String s) {
        String[] split = s.split("\r\n");
        String s1 = split[split.length - 1].replaceAll("(\\r|\\n)", "");
        if (s1.isEmpty()) {
            String[] strings = ArrayUtils.removeElement(split, split.length - 1);
            return Joiner.on("\r\n").join(strings).replaceAll("(\\r|\\n)", "");
        } else {
            return s.replaceAll("(\\r|\\n)", "");
        }
    }
}
