package com.comandante.creeper.cclient;

import com.comandante.creeper.chat.Utils;
import com.terminal.TerminalMode;
import com.terminal.ui.JediTermWidget;
import com.terminal.ui.TerminalSession;
import com.terminal.ui.TerminalWidget;
import com.terminal.ui.settings.DefaultTabbedSettingsProvider;
import com.terminal.ui.settings.SettingsProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.Color;

public class CreeperTerminal extends JediTermWidget {

    private final String name;
    private final SimpleTtyConnector simpleTtyConnector;

    public CreeperTerminal(String name) {
        super(new DefaultTabbedSettingsProvider());
        this.getTerminal().reset();
        this.getTerminalDisplay().setCursorVisible(false);
        this.getTerminal().setModeEnabled(TerminalMode.ANSI, true);
        this.getTerminal().setAnsiConformanceLevel(2);
        this.name = name;
        this.simpleTtyConnector = new SimpleTtyConnector();
        if (this.canOpenSession()) {
            TerminalSession session = this.createTerminalSession(simpleTtyConnector);
            session.start();
        }
    }

    public void append(String append) {
        try {
            simpleTtyConnector.write(append);
            if (this.getTerminal().getCursorX() > 1) {
                this.getTerminal().newLine();
            }
            int cursorY = this.getTerminal().getCursorY();
            this.getTerminal().cursorPosition(0, cursorY);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
