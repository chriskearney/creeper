package com.comandante.creeper.command;

import java.util.Set;

public interface CommandType {
    String getDescription();

    Set<String> getValidCommandTriggers();

    boolean isCaseSensitive();

}
