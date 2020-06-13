package com.terminal.ui;

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class MainMenubar extends JMenuBar {


    private final JMenuItem connectDisconnect;

    public MainMenubar() {
        this.connectDisconnect = new JMenuItem("Connect");
        add(connectDisconnect);

    }
}
