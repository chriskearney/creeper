package com.comandante.creeper.cclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

import javax.swing.JLabel;
import java.awt.Color;
import java.io.IOException;
import java.util.Set;
import java.util.StringJoiner;

import static com.terminal.ui.ColorPane.getTerminalFont;

public class MapStatusBar extends JLabel {

    private final ObjectMapper objectMapper;

    public MapStatusBar(String text, ObjectMapper objectMapper) {
        super(text);
        this.objectMapper = objectMapper;
        setOpaque(true);
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setFont(getTerminalFont());
        setHorizontalAlignment(LEFT);
    }

    @Subscribe
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.PLAYERDATA)) {
            return;
        }
        JsonNode jsonNode = objectMapper.readValue(creeperEvent.getPayload(), JsonNode.class);
        long currentRoomId = jsonNode.get("currentRoomId").asLong();
        Set<String> currentAreas = Sets.newHashSet();
        jsonNode.get("currentAreas").forEach(jsonNode1 -> currentAreas.add(jsonNode1.asText()));
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String s: currentAreas) {
            stringJoiner.add(s);
        }

        setText("id: " + currentRoomId + " areas: " + stringJoiner.toString());
    }
}
