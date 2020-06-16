package com.comadante.creeper.cclient;

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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;
import java.util.function.Supplier;

public class ConsolePanel extends JPanel {

    private final ConsoleStatusBar consoleStatusBar;
    private final JediTermWidget jediTermWidget;
    private final List<JediEmulator.NonControlCharListener> nonControlCharListeners;
    private final Supplier<TtyConnector> ttyConnectorSupplier;
    private final Input input;
    private final TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Console");;

    public ConsolePanel(ConsoleStatusBar consoleStatusBar,
                        MapPanel.MapWindowMovementHandler mapWindowMovementHandler,
                        List<JediEmulator.NonControlCharListener> nonControlCharListeners,
                        Supplier<TtyConnector> ttyConnectorSupplier
    ) {
        this.consoleStatusBar = consoleStatusBar;
        this.nonControlCharListeners = nonControlCharListeners;
        this.jediTermWidget = createTerminalWidget(new DefaultTabbedSettingsProvider(), nonControlCharListeners);
        this.ttyConnectorSupplier = ttyConnectorSupplier;
        this.border.setTitleColor(Color.white);

        jediTermWidget.getTerminalDisplay().setCursorVisible(false);
        jediTermWidget.getTerminalDisplay().setScrollingEnabled(true);
        setBackground(Color.black);
        setBorder(border);
        this.input = new Input(line -> {
            try {
                jediTermWidget.getCurrentSession().getTtyConnector().write(line + "\n");
                jediTermWidget.getMyScrollBar().setValue(jediTermWidget.getMyScrollBar().getMaximum());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Problem writing to the com.terminal.");
            }
        },mapWindowMovementHandler);
        consoleStatusBar.setBackground(Color.darkGray);
        JPanel inputAndStatus = new JPanel(new BorderLayout());
        inputAndStatus.add(input, BorderLayout.PAGE_END);
        inputAndStatus.add(consoleStatusBar, BorderLayout.PAGE_START);
        setLayout(new BorderLayout());
        add(jediTermWidget.getComponent(), BorderLayout.CENTER);
        add(inputAndStatus, BorderLayout.PAGE_END);
        setVisible(true);
        openSession(jediTermWidget);
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

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        reset();
    }
}
