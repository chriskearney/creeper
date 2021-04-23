package com.comandante.creeper.cclient;

import com.comandante.creeper.chat.Gossip;
import com.comandante.creeper.chat.Utils;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ResetEvent;
import com.terminal.ui.TerminalSession;
import com.terminal.ui.TerminalWidget;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GossipWindow extends JFrame {

    private final CreeperTerminal creeperTerminal;
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GossipWindow.class);

    public GossipWindow(Input input, GossipUserPanel gossipUserPanel) throws IOException {
        this.creeperTerminal = new CreeperTerminal("Gossip");
        setTitle("Gossip");
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        input.setRequestFocusEnabled(true);
        input.setFocusable(true);
        gossipUserPanel.setPreferredSize(new Dimension(150, 236));
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));
        jPanel.add(creeperTerminal);
        jPanel.add(input);
        jPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Gossip"));
        jPanel.setBackground(Color.BLACK);
        add(jPanel, BorderLayout.CENTER);
        add(gossipUserPanel, BorderLayout.LINE_END);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        jPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                input.getField().requestFocus();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                input.getField().requestFocus();
            }
        });

        gossipUserPanel.addMouseListener(new MouseAdapter() {
        });

        setPreferredSize(CreeperClientMainFrame.MAIN_FRAME_HALF);
        pack();
    }

    public void appendChatMessage(Long timestamp, String name, String message) {
        creeperTerminal.append(Utils.buildGossipString(timestamp.toString(), name, message));
    }

    @Subscribe
    public void creeperEvent(Gossip gossip) {
        appendChatMessage(gossip.getTimestamp(), gossip.getName(), gossip.getMessage());
    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
    }
}
