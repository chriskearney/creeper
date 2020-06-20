package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
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
    public void creeperEvent(PlayerData playerData) throws IOException {

        long currentRoomId = playerData.getCurrentRoomId();
        Set<String> currentAreas = Sets.newHashSet();
        playerData.getCurrentAreas().forEach(area -> {
            currentAreas.add(area.getName());
        });
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String s: currentAreas) {
            stringJoiner.add(s);
        }

        setText("id: " + currentRoomId + " areas: " + stringJoiner.toString());
    }
}
