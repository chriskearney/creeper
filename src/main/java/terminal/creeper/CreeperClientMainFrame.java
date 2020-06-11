package terminal.creeper;

import org.apache.log4j.Logger;
import terminal.ui.AbstractTerminalFrame;
import terminal.ui.UIUtil;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class CreeperClientMainFrame {

    private final static Logger LOG = Logger.getLogger(AbstractTerminalFrame.class);
    private final static Boolean LOG_LOCATION_DETAILS = false;
    private final static Dimension GOSSIP_WINDOW = new Dimension(752, 379);
    private final static Dimension MAP_WINDOW = new Dimension(380, 380);
    private final static Dimension MAIN_TOOLBAR = new Dimension(Integer.MAX_VALUE, 30);
    private final static Dimension MAIN_FRAME = new Dimension(1142, 1135);
    private final static Dimension CONSOLE_WINDOW = new Dimension(851, 681);
    private final static Dimension INVENTORY_WINDOW = new Dimension(273, 374);
    private final static Dimension STATS_WINDOW = new Dimension(378, 496);
    private final static Dimension NEARME_WINDOW = new Dimension(273, 305);

    //java.awt.Dimension[width=283,height=281]

    private final static Point CONSOLE_WINDOW_POINT = new Point(282, 4);

    protected CreeperClientMainFrame(ConsoleWindow consoleWindow,
                                     GossipWindow gossipWindow,
                                     MapWindow mapWindow,
                                     StatsWindow statsWindow,
                                     MainToolbar mainToolbar,
                                     InventoryWindow inventoryWindow,
                                     NearWindow nearWindow) {

        JDesktopPane jDesktopPane = new JDesktopPane();

        consoleWindow.setPreferredSize(CONSOLE_WINDOW);
        consoleWindow.setLocation(CONSOLE_WINDOW_POINT);
        consoleWindow.pack();

        gossipWindow.setPreferredSize(GOSSIP_WINDOW);
        gossipWindow.setLocation(new Point(384, 690));
        gossipWindow.setVisible(true);
        gossipWindow.pack();

        mapWindow.setPreferredSize(MAP_WINDOW);
        mapWindow.setLocation(new Point(2, 689));
        mapWindow.setVisible(true);
        mapWindow.pack();

        statsWindow.setPreferredSize(STATS_WINDOW);
        statsWindow.pack();

        inventoryWindow.setPreferredSize(INVENTORY_WINDOW);
        inventoryWindow.setLocation(new Point(4, 4));
        inventoryWindow.setVisible(true);
        inventoryWindow.pack();

        nearWindow.setPreferredSize(NEARME_WINDOW);
        nearWindow.setLocation(new Point(4, 381));
        nearWindow.pack();

        mainToolbar.setPreferredSize(MAIN_TOOLBAR);

        jDesktopPane.add(consoleWindow);
        jDesktopPane.add(mapWindow);
        jDesktopPane.add(gossipWindow);
        jDesktopPane.add(inventoryWindow);
        jDesktopPane.add(statsWindow);
        jDesktopPane.add(nearWindow);
        jDesktopPane.setBackground(Color.DARK_GRAY);

        final JFrame mainFrame = new JFrame("Creeper");

        JPanel jPanel = new JPanel();
        jPanel.add(mainToolbar);
        jPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(jDesktopPane, BorderLayout.CENTER);
        mainFrame.add(jPanel, BorderLayout.PAGE_START);
        mainFrame.setPreferredSize(MAIN_FRAME);
        mainFrame.pack();
        mainFrame.setVisible(true);

        if (LOG_LOCATION_DETAILS) {
            jDesktopPane.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(final ComponentEvent e) {
                    super.componentResized(e);
                    System.out.println("DESKTOP PANE: + " + e.getComponent().getSize().toString());
                }
            });
            attachStdoutSizeAndPositionLogger(gossipWindow);
            attachStdoutSizeAndPositionLogger(mapWindow);
            attachStdoutSizeAndPositionLogger(statsWindow);
            attachStdoutSizeAndPositionLogger(consoleWindow);
            attachStdoutSizeAndPositionLogger(nearWindow);
            attachStdoutSizeAndPositionLogger(inventoryWindow);
        }

    }

    public void attachStdoutSizeAndPositionLogger(JInternalFrame f) {
        f.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                super.componentResized(e);
                Dimension size = e.getComponent().getSize();
                System.out.println(size.toString());
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                super.componentMoved(e);
                int x = e.getComponent().getX();
                int y = e.getComponent().getY();
                System.out.println("x: " + x + " y: " + y);
                Dimension size = e.getComponent().getSize();
                System.out.println(size.toString());
            }
        });
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
        return new Font(fontName, Font.PLAIN, 14);
    }

}
