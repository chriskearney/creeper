package terminal.creeper;

import com.google.common.eventbus.Subscribe;
import terminal.ui.ColorPane;
import terminal.ui.ResetEvent;

import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class MapWindow extends JInternalFrame {
    private final ColorPane colorPane;
    private final MapStatusBar mapStatusBar;
    private final MapWindowMovementHandler mapWindowMovementHandler;

    public MapWindow(MapStatusBar mapStatusBar, MapWindowMovementHandler mapWindowMovementHandler) {
        this.mapWindowMovementHandler = mapWindowMovementHandler;
        this.colorPane = new ColorPane();
        this.mapStatusBar = mapStatusBar;
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        add(colorPane);
        // add(mapStatusBar);
        putClientProperty("JInternalFrame.frameType", "normal");
        Dimension dimension = new Dimension(380, 400);
        setBackground(Color.BLACK);
        setPreferredSize(dimension);
        setIconifiable(true);
        setClosable(true);
        setTitle("Map");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        pack();

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
    }


    @Subscribe
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.DRAW_MAP)) {
            return;
        }
        colorPane.appendANSI(creeperEvent.getPayload());
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
