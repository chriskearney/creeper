package com.comadante.creeper.cclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.eventbus.Subscribe;
import com.terminal.TtyConnector;
import com.terminal.emulator.JediEmulator;
import com.terminal.ui.JediTermWidget;
import com.terminal.ui.ResetEvent;
import com.terminal.ui.TerminalSession;
import com.terminal.ui.TerminalWidget;
import com.terminal.ui.settings.DefaultTabbedSettingsProvider;
import com.terminal.ui.settings.TabbedSettingsProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.function.Supplier;

public class ConsolePanel extends JPanel {

    private final ObjectMapper objectMapper;

    private final ConsoleStatusBar consoleStatusBar;
    private final JediTermWidget jediTermWidget;
    private final List<JediEmulator.NonControlCharListener> nonControlCharListeners;
    private final Supplier<TtyConnector> ttyConnectorSupplier;
    private final Input input;
    private final TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Console");;

    public ConsolePanel(ConsoleStatusBar consoleStatusBar,
                        MapPanel.MapWindowMovementHandler mapWindowMovementHandler,
                        List<JediEmulator.NonControlCharListener> nonControlCharListeners,
                        Supplier<TtyConnector> ttyConnectorSupplier,
                        ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.consoleStatusBar = consoleStatusBar;
        this.nonControlCharListeners = nonControlCharListeners;
        this.jediTermWidget = createTerminalWidget(new DefaultTabbedSettingsProvider(), nonControlCharListeners);
        this.ttyConnectorSupplier = ttyConnectorSupplier;

        jediTermWidget.getTerminalDisplay().setCursorVisible(false);
        jediTermWidget.getTerminalDisplay().setScrollingEnabled(true);

        consoleStatusBar.setBackground(Color.darkGray);
        JPanel inputAndStatus = new JPanel(new BorderLayout());
        this.input = new Input(line -> {
            try {
                jediTermWidget.getCurrentSession().getTtyConnector().write(line + "\n");
                jediTermWidget.getMyScrollBar().setValue(jediTermWidget.getMyScrollBar().getMaximum());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Problem writing to the com.terminal.");
            }
        },mapWindowMovementHandler);


        inputAndStatus.add(input, BorderLayout.PAGE_END);
        inputAndStatus.add(consoleStatusBar, BorderLayout.PAGE_START);


        this.setBackground(Color.black);
        this.border.setTitleColor(Color.white);
        this.setBorder(border);
        this.setLayout(new BorderLayout());
        this.add(jediTermWidget.getComponent(), BorderLayout.CENTER);
        this.add(inputAndStatus, BorderLayout.PAGE_END);
        this.setFocusable(false);


        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                getInput().getField().requestFocus();
            }
        });

//        addKeyListener(new java.awt.event.KeyAdapter() {
//            public void keyPressed(java.awt.event.KeyEvent evt) {
//                System.out.println("hi");
//                try {
//                    SwingUtilities.invokeLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            getInput().getField().requestFocus();
//                            getJediTermWidget().getMyScrollBar().setValue(getJediTermWidget().getMyScrollBar().getMaximum());
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        this.setVisible(true);
        this.openSession(jediTermWidget);
    }


    protected JediTermWidget createTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider, List<JediEmulator.NonControlCharListener> nonControlCharListeners) {
        return new JediTermWidget(settingsProvider, nonControlCharListeners);
    }

    protected void openSession(TerminalWidget terminal) {
        if (terminal.canOpenSession()) {
            openSession(terminal, ttyConnectorSupplier.get());
        }
    }

    public void openSession(TerminalWidget terminal, TtyConnector ttyConnector) {
        TerminalSession session = terminal.createTerminalSession(ttyConnector);
        session.start();
    }

    public void connect() {
        this.openSession(jediTermWidget);
    }

    private void reset() {
        try {
            jediTermWidget.getCurrentSession().getTtyConnector().close();
            jediTermWidget.getCurrentSession().getTerminal().writeCharacters("\n" + "Disconnected." + "\n");
        } catch (Exception e) {
            System.out.println("failed to write to com.terminal tty");
        }

    }

    public JediTermWidget getJediTermWidget() {
        return jediTermWidget;
    }

    public Input getInput() {
        return input;
    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        reset();
    }

    @Subscribe
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.PLAYERDATA)) {
            return;
        }
        JsonNode jsonNode = objectMapper.readValue(creeperEvent.getPayload(), JsonNode.class);
        Boolean isInFight = jsonNode.get("inFight").asBoolean();
        if (isInFight) {
            border.setBorder(BorderFactory.createLineBorder(Color.red));
            repaint();
        } else {
            border.setBorder(BorderFactory.createLineBorder(Color.green));
            repaint();
        }
//        java.util.List<String> coolDowns = Lists.newArrayList();
//        if (jsonNode.get("playerMetadata").has("coolDownMap")) {
//            jsonNode.get("playerMetadata").get("coolDownMap").forEach(j -> {
//                if (j.get("active").asBoolean()) {
//                    coolDowns.add(j.get("coolDownType").asText());
//                } else {
//                    System.out.println("Found inactive cooldown: " + j.get("coolDownType").asText());
//                    border.setBorder(BorderFactory.createLineBorder(Color.green));
//                    repaint();
//                }
//            });
//        }
    }
}
