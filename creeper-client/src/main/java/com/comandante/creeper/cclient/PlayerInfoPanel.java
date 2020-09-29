package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ColorPane;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

public class PlayerInfoPanel extends JPanel {

    private final ColorPane colorPane;

    private String ornateName;
    private String ornateLevelAndClass;

    public PlayerInfoPanel() {
        this.colorPane = new ColorPane();
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);
        add(colorPane);
        setFocusable(false);
        setVisible(true);
    }

    @Subscribe
    public void creeperEvent(PlayerData playerData) {
        boolean render = false;
        if (!playerData.getOrnatePlayerName().equals(ornateName)) {
            this.ornateName = playerData.getOrnatePlayerName();
            render = true;
        }

        if (!playerData.getOrnateLevelAndClass().equals(ornateLevelAndClass)) {
            this.ornateLevelAndClass = playerData.getOrnateLevelAndClass();
            render = true;
        }

        if (render) {
            colorPane.appendANSI(ornateName + "\r\n" + ornateLevelAndClass);
        }
    }
}
