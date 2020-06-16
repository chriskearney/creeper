package com.terminal.ui;

import com.fasterxml.jackson.databind.JsonNode;

public interface GetDataListener {
    void process(JsonNode jsonNode);
}

