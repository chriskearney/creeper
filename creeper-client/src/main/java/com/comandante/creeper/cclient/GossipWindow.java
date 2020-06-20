package com.comandante.creeper.cclient;

import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import com.terminal.TerminalMode;
import com.terminal.emulator.JediEmulator;
import com.terminal.ui.JediTermWidget;
import com.terminal.ui.ResetEvent;
import com.terminal.ui.TerminalSession;
import com.terminal.ui.TerminalWidget;
import com.terminal.ui.settings.DefaultTabbedSettingsProvider;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;
import java.util.Collections;

import static com.comandante.creeper.server.player_communication.Color.CYAN;
import static com.comandante.creeper.server.player_communication.Color.MAGENTA;
import static com.comandante.creeper.server.player_communication.Color.RESET;
import static com.comandante.creeper.server.player_communication.Color.WHITE;

public class GossipWindow extends JFrame {

    private final ObjectMapper objectMapper;
    private final SimpleTtyConnector simpleTtyConnector = new SimpleTtyConnector("Gossip");
    private final JediTermWidget jediTermWidget;

    private final TitledBorder mainBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Gossip");


    public GossipWindow(Input input, ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;
        setTitle("Gossip");
        JediEmulator.NonControlCharListener nonControlCharListener = new JediEmulator.NonControlCharListener() {
            @Override
            public void processNonControlChar(String nonControlCharacters) {

            }
        };
        this.jediTermWidget = new JediTermWidget(new DefaultTabbedSettingsProvider(), Collections.singletonList(nonControlCharListener));
        jediTermWidget.getTerminal().reset();
        jediTermWidget.getTerminalDisplay().setCursorVisible(false);
        jediTermWidget.getTerminal().setModeEnabled(TerminalMode.ANSI, true);
        jediTermWidget.getTerminal().setAnsiConformanceLevel(2);
        openSession(jediTermWidget, simpleTtyConnector);

        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        input.setRequestFocusEnabled(true);
        input.setFocusable(true);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));
        jPanel.add(jediTermWidget);
        jPanel.add(input);
        jPanel.setBorder(mainBorder);
        jPanel.setBackground(Color.BLACK);
        add(jPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(HIDE_ON_CLOSE);

        jediTermWidget.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent aE) {
                input.getField().requestFocus();
            }
        });

        setPreferredSize(CreeperClientMainFrame.MAIN_FRAME_HALF);

        pack();
    }

    protected void openSession(TerminalWidget terminal, SimpleTtyConnector simpleTtyConnector) {
        if (terminal.canOpenSession()) {
            TerminalSession session = terminal.createTerminalSession(simpleTtyConnector);
            session.start();
        }
    }

    public void appendChatMessage(Long timestamp, String name, String message) {
        try {
            String gossipMessage = WHITE + "[" + RESET + MAGENTA + name + WHITE + "] " + RESET + CYAN + message + RESET;
            simpleTtyConnector.write(gossipMessage);
            jediTermWidget.getTerminal().newLine();
            int cursorY = jediTermWidget.getTerminal().getCursorY();
            jediTermWidget.getTerminal().cursorPosition(0, cursorY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Subscribe
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.GOSSIP)) {
            return;
        }
        JsonNode jsonNode = objectMapper.readValue(creeperEvent.getPayload(), JsonNode.class);
        String name = jsonNode.get("name").asText();
        String message = jsonNode.get("message").asText();
        Long timestamp = jsonNode.get("name").asLong();
        appendChatMessage(timestamp, name, message);
    }


    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
    }
}
