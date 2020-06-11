package terminal.creeper;

import com.google.common.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;
import terminal.RequestOrigin;
import terminal.TtyConnector;
import terminal.emulator.JediEmulator;
import terminal.ui.JediTermWidget;
import terminal.ui.ResetEvent;
import terminal.ui.TerminalPanelListener;
import terminal.ui.TerminalSession;
import terminal.ui.TerminalWidget;
import terminal.ui.settings.DefaultTabbedSettingsProvider;
import terminal.ui.settings.TabbedSettingsProvider;

import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.function.Supplier;

public class ConsoleWindow extends JInternalFrame {

    private final ConsoleStatusBar consoleStatusBar;
    private final TerminalWidget myTerminal;
    private final List<JediEmulator.NonControlCharListener> nonControlCharListeners;
    private final Supplier<TtyConnector> ttyConnectorSupplier;

    public ConsoleWindow(ConsoleStatusBar consoleStatusBar,
                         List<JediEmulator.NonControlCharListener> nonControlCharListeners,
                         Supplier<TtyConnector> ttyConnectorSupplier
    ) {
        this.consoleStatusBar = consoleStatusBar;
        this.nonControlCharListeners = nonControlCharListeners;
        this.myTerminal = createTerminalWidget(new DefaultTabbedSettingsProvider(), nonControlCharListeners);
        this.ttyConnectorSupplier = ttyConnectorSupplier;

        myTerminal.getTerminalDisplay().setCursorVisible(false);

        setTitle("Console");
        BorderLayout borderLayout = new BorderLayout();
        borderLayout.setVgap(0);
        getContentPane().setLayout(borderLayout);
        getContentPane().add(myTerminal.getComponent(), BorderLayout.CENTER);
        putClientProperty("JInternalFrame.frameType", "normal");
        setMaximizable(true);
        Input input = new Input(line -> {
            try {
                myTerminal.getCurrentSession().getTtyConnector().write(line + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Problem writing to the terminal.");
            }
        });
        JPanel jPanel = new JPanel();
        jPanel.setBackground(Color.BLACK);
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.add(input);
        jPanel.add(consoleStatusBar);
        getContentPane().add(jPanel, BorderLayout.PAGE_END);
        //frame.get
        sizeFrameForTerm(this);

        //jInternalFrame.setLocationByPlatform(true);
        setVisible(true);

        setResizable(true);
        setIconifiable(true);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameActivated(InternalFrameEvent e) {
                input.getField().requestFocus();
                super.internalFrameActivated(e);
            }
        });
        myTerminal.setTerminalPanelListener(new TerminalPanelListener() {
            public void onPanelResize(final Dimension pixelDimension, final RequestOrigin origin) {
            }

            @Override
            public void onSessionChanged(final TerminalSession currentSession) {
                // this.setTitle(currentSession.getSessionName());
            }

            @Override
            public void onTitleChanged(String title) {
                //mainConsoleInternalFrame.setTitle(myTerminal.getCurrentSession().getSessionName());
            }
        });

        openSession(myTerminal);
        pack();
    }

    private void sizeFrameForTerm(final JInternalFrame frame) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Dimension d = myTerminal.getPreferredSize();

                d.width = frame.getWidth() - frame.getContentPane().getWidth();
                d.height = frame.getHeight() - frame.getContentPane().getHeight();
                frame.setSize(d);
            }
        });
    }


    protected JediTermWidget createTerminalWidget(@NotNull TabbedSettingsProvider settingsProvider, java.util.List<JediEmulator.NonControlCharListener> nonControlCharListeners) {
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
        this.openSession(myTerminal);
    }

    private void reset() {
        try {
            myTerminal.getCurrentSession().getTtyConnector().close();
            myTerminal.getCurrentSession().getTerminal().writeCharacters("\n" + "Disconnected." + "\n");
        } catch (Exception e) {
            System.out.println("failed to write to terminal tty");
        }

    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        reset();
    }
}
