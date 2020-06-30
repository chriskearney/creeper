package com.comandante.creeper.cclient;

import com.comandante.creeper.chat.Gossip;
import com.comandante.creeper.chat.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.Subscribe;
import com.terminal.TerminalMode;
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
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.IOException;

public class GossipWindow extends JFrame {

    private final SimpleTtyConnector simpleTtyConnector = new SimpleTtyConnector("Gossip");
    private final JediTermWidget jediTermWidget;

    private final TitledBorder mainBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Gossip");

    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GossipWindow.class);

    public GossipWindow(Input input, GossipUserPanel gossipUserPanel) throws IOException {
        this.jediTermWidget = new JediTermWidget(new DefaultTabbedSettingsProvider());
        this.jediTermWidget.getTerminal().reset();
        this.jediTermWidget.getTerminalDisplay().setCursorVisible(false);
        this.jediTermWidget.getTerminal().setModeEnabled(TerminalMode.ANSI, true);
        this.jediTermWidget.getTerminal().setAnsiConformanceLevel(2);

        openSession(jediTermWidget, simpleTtyConnector);

        setTitle("Gossip");
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        input.setRequestFocusEnabled(true);
        input.setFocusable(true);

        gossipUserPanel.setPreferredSize(new Dimension(150, 236));


        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));
        jPanel.add(jediTermWidget);
        jPanel.add(input);
        jPanel.setBorder(mainBorder);
        jPanel.setBackground(Color.BLACK);
        add(jPanel, BorderLayout.CENTER);
        add(gossipUserPanel, BorderLayout.LINE_END);

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
            simpleTtyConnector.write(Utils.buildGossipString(timestamp.toString(), name, message));
            jediTermWidget.getTerminal().newLine();
            int cursorY = jediTermWidget.getTerminal().getCursorY();
            jediTermWidget.getTerminal().cursorPosition(0, cursorY);
        } catch (Exception e) {
            LOG.error("Unable to append chat message.", e);
        }
    }

    @Subscribe
    public void creeperEvent(Gossip gossip) throws IOException {
        appendChatMessage(gossip.getTimestamp(), gossip.getName(), gossip.getMessage());
    }


    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
    }
}
