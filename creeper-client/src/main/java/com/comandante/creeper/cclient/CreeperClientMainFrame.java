package com.comandante.creeper.cclient;

import com.terminal.ui.AbstractTerminalFrame;
import com.terminal.ui.UIUtil;
import org.apache.log4j.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class CreeperClientMainFrame extends JFrame {

    private final static Logger LOG = Logger.getLogger(CreeperClientMainFrame.class);

    public final static Dimension MAIN_FRAME = new Dimension(1280, 720);
    public final static Dimension MAIN_FRAME_HALF = new Dimension(1280, 410);
    public final static Dimension RIGHT_SIDE_PANEL_DIMENSIONS = new Dimension(270, 215);
    public final static Dimension RIGHT_SIDE_PANEL_DIMENSIONS_BIGGER = new Dimension(270, 236);
    protected CreeperClientMainFrame(ConsolePanel consolePanel,
                                     GossipWindow gossipWindow,
                                     BattlePanel battlePanel,
                                     MapPanel mapPanel,
                                     StatsWindow statsWindow,
                                     InventoryPanel inventoryPanel,
                                     NearPanel nearPanel) {

        JPanel rightSidePanel = new JPanel();
        rightSidePanel.setLayout(new BoxLayout(rightSidePanel, BoxLayout.PAGE_AXIS));
        rightSidePanel.add(inventoryPanel);
        rightSidePanel.add(nearPanel);
        rightSidePanel.add(mapPanel);
        rightSidePanel.setBackground(Color.BLACK);

        CreeperClientMainMenuBar creeperClientMainMenuBar = new CreeperClientMainMenuBar(gossipWindow, null);
        setJMenuBar(creeperClientMainMenuBar);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(battlePanel, BorderLayout.LINE_START);
        add(consolePanel, BorderLayout.CENTER);
        add(rightSidePanel, BorderLayout.LINE_END);
        setBackground(Color.BLACK);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> consolePanel.getInput().getField().requestFocus());
            }
        });

        setMinimumSize(MAIN_FRAME);
        setPreferredSize(MAIN_FRAME);

        pack();
        setVisible(true);
    }
}
