package com.comandante.creeper.cclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ColorPane;
import com.terminal.ui.ResetEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;

import static com.comandante.creeper.cclient.CreeperClientMainFrame.RIGHT_SIDE_PANEL_DIMENSIONS;

public class MapPanel extends JPanel {
    private final ObjectMapper objectMapper;
    private final ColorPane colorPane;
    private final MapStatusBar mapStatusBar;
    private final MapWindowMovementHandler mapWindowMovementHandler;
    private final TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Map");


    public MapPanel(MapStatusBar mapStatusBar, MapWindowMovementHandler mapWindowMovementHandler, ObjectMapper objectMapper) {
        this.mapWindowMovementHandler = mapWindowMovementHandler;
        this.colorPane = new ColorPane();
        this.mapStatusBar = mapStatusBar;
        this.objectMapper = objectMapper;
        this.border.setTitleColor(Color.white);

        setBorder(border);
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        setPreferredSize(RIGHT_SIDE_PANEL_DIMENSIONS);
        setMaximumSize(RIGHT_SIDE_PANEL_DIMENSIONS);
        setMinimumSize(RIGHT_SIDE_PANEL_DIMENSIONS);

        add(colorPane, BorderLayout.CENTER);

        this.colorPane.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case 37:
                        mapWindowMovementHandler.west();
                        break;
                    case 38:
                        mapWindowMovementHandler.north();
                        break;
                    case 39:
                        mapWindowMovementHandler.east();
                        break;
                    case 40:
                        mapWindowMovementHandler.south();
                        break;
                }
            }
        });
        setFocusable(false);
    }


    @Subscribe
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        if (creeperEvent.getCreeperEventType().equals(CreeperEventType.PLAYERDATA)) {

            JsonNode jsonNode = objectMapper.readValue(creeperEvent.getPayload(), JsonNode.class);
            Boolean isInFight = jsonNode.get("inFight").asBoolean();
            if (isInFight) {
                border.setBorder(BorderFactory.createLineBorder(Color.red));
                repaint();
            } else {
                border.setBorder(BorderFactory.createLineBorder(Color.green));
                repaint();
            }
        }

        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.DRAW_MAP)) {
            return;
        }
        colorPane.appendANSI(creeperEvent.getPayload());
    }

    public MapWindowMovementHandler getMapWindowMovementHandler() {
        return mapWindowMovementHandler;
    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        colorPane.appendANSI("");
    }

    public interface MapWindowMovementHandler {

        void north();

        void south();

        void east();

        void west();

    }


    // UP - 38
    // DOWN - 40
    // LEFT - 37
    // RIGHT - 39

}
