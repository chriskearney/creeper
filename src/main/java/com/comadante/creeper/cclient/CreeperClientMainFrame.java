package com.comadante.creeper.cclient;

import com.terminal.ui.AbstractTerminalFrame;
import com.terminal.ui.UIUtil;
import org.apache.log4j.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

public abstract class CreeperClientMainFrame extends JFrame {

    private final static Logger LOG = Logger.getLogger(AbstractTerminalFrame.class);
    public final static Dimension MAIN_FRAME = new Dimension(1142, 1135);
    public final static Dimension RIGHT_SIDE_PANEL_DIMENSIONS = new Dimension(270, 214);
    public final static Dimension RIGHT_SIDE_PANEL_DIMENSIONS_BIGGER = new Dimension(270, 236);
    protected CreeperClientMainFrame(ConsolePanel consoleWindow,
                                     GossipWindow gossipWindow,
                                     MapPanel mapPanel,
                                     StatsWindow statsWindow,
                                     MainToolbar mainToolbar,
                                     InventoryPanel inventoryPanel,
                                     NearPanel nearPanel) {



        JPanel rightSidePanel = new JPanel();
        rightSidePanel.setLayout(new BoxLayout(rightSidePanel, BoxLayout.PAGE_AXIS));
        rightSidePanel.add(inventoryPanel);
        rightSidePanel.add(nearPanel);
        rightSidePanel.add(mapPanel);
        rightSidePanel.setBackground(Color.BLACK);
//        Dimension dimension = new Dimension(274, 206);
//        rightSidePanel.setPreferredSize(dimension);
//        rightSidePanel.setMinimumSize(dimension);
//        rightSidePanel.setMaximumSize(dimension);


        CreeperClientMainMenuBar creeperClientMainMenuBar = new CreeperClientMainMenuBar();
        setJMenuBar(creeperClientMainMenuBar);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(consoleWindow, BorderLayout.CENTER);
        add(rightSidePanel, BorderLayout.LINE_END);
        setBackground(Color.BLACK);
        setVisible(true);
        pack();
    }

    public static Font getTerminalFont() {
        String fontName;
        if (UIUtil.isWindows) {
            fontName = "Consolas";
        } else if (UIUtil.isMac) {
            fontName = "Menlo";
        } else {
            fontName = "Monospaced";
        }
        return new Font(fontName, Font.PLAIN, 12);
    }

}
