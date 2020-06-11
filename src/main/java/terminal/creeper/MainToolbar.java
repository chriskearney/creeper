package terminal.creeper;

import com.google.common.collect.Maps;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class MainToolbar extends JToolBar implements ActionListener {

    private final Map<String, JComponent> jComponentMap = Maps.newHashMap();
    private final ConnectAction connectAction;
    private final DisconnectAction disconnectAction;

    public MainToolbar(ConnectAction connectAction, DisconnectAction disconnectAction) {
        this.connectAction = connectAction;
        this.disconnectAction = disconnectAction;
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        addButtons(this);
    }

    public void addManagedComponent(String actionCommand, JComponent jComponent) {
        jComponentMap.put(actionCommand, jComponent);
    }

    protected void addButtons(JToolBar toolBar) {
        JButton connect = null;

        connect = makeButton("CONNECT", "Connect");
        toolBar.add(connect);

        connect = makeButton("DISCONNECT", "Disconnect");
        toolBar.add(connect);

        connect = makeButton("MAP", "Map");
        toolBar.add(connect);

        connect = makeButton("STATS", "Stats");
        toolBar.add(connect);

        connect = makeButton("INVENTORY", "Inventory");
        toolBar.add(connect);

        connect = makeButton("GOSSIP", "Gossip");
        toolBar.add(connect);

        connect = makeButton("NEAR", "Near");
        toolBar.add(connect);
    }

    protected JButton makeButton(
            String actionCommand,
            String text) {
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.addActionListener(this);
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setFont(CreeperClientMainFrame.getTerminalFont());
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setText(text);

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComponent jComponent = jComponentMap.get(e.getActionCommand());
        if (jComponent != null) {
            jComponent.setVisible(!jComponent.isVisible());
            jComponent.revalidate();
            return;
        }
        switch (e.getActionCommand()) {
            case "CONNECT":
                connectAction.connect();
                break;
            case "DISCONNECT":
                disconnectAction.disconnect();
                break;
        }
    }

    public interface DisconnectAction {
        void disconnect();
    }

    public interface ConnectAction {
        void connect();
    }
}