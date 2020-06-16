package com.comadante.creeper.cclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ColorPane;
import com.terminal.ui.ResetEvent;

import javax.swing.JInternalFrame;
import java.awt.BorderLayout;
import java.awt.Point;
import java.io.IOException;

public class StatsWindow extends JInternalFrame{

    private final ColorPane statsPane;
    private final ObjectMapper objectMapper;

    public StatsWindow(ObjectMapper objectMapper) {
        this.statsPane = new ColorPane();
        this.objectMapper = objectMapper;
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(statsPane, BorderLayout.CENTER);
        putClientProperty("JInternalFrame.frameType", "normal");
        setLocation(new Point(1, 2));
        setResizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setClosable(true);
        setFocusable(false);
        pack();
    }


    @Subscribe
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.PLAYERDATA)) {
            return;
        }
        JsonNode jsonNode = objectMapper.readValue(creeperEvent.getPayload(), JsonNode.class);
        statsPane.appendANSI(jsonNode.get("lookSelf").asText());
    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        statsPane.appendANSI("");
    }
}
