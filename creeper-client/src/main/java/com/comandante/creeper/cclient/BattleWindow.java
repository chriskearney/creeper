package com.comandante.creeper.cclient;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BattleWindow extends JFrame {

    private final CreeperTerminal creeperTerminal;
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BattleWindow.class);

    public BattleWindow() {
        this.creeperTerminal = new CreeperTerminal("Battle");
        setTitle("Battle");
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JPanel jPanelTop = new JPanel();
        jPanelTop.setLayout(new BoxLayout(jPanelTop, BoxLayout.PAGE_AXIS));
        jPanelTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Battle Log"));
        jPanelTop.add(creeperTerminal);
        jPanelTop.setBackground(Color.BLACK);

        BufferedImage image;
        try {
            image = ImageIO.read(BattleWindow.class.getResourceAsStream("/dragon.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        add(new ImagePanel(image), BorderLayout.CENTER);
        add(jPanelTop, BorderLayout.PAGE_END);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setPreferredSize(new Dimension(400, 720));
        setMinimumSize(new Dimension(400, 720));
        setMaximumSize(new Dimension(400, 720));
        pack();
    }
}
