package com.comandante.creeper.cclient;

import com.comandante.creeper.chat.Gossip;
import com.comandante.creeper.events.PlayerData;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class BattleWindow extends JFrame {

    private final CreeperTerminal creeperTerminal;
    private final CreeperApiHttpClient creeperApiHttpClient;
    private final ImagePanel imagePanel;

    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BattleWindow.class);

    public BattleWindow(CreeperApiHttpClient creeperApiHttpClient) {
        this.creeperApiHttpClient = creeperApiHttpClient;
        this.creeperTerminal = new CreeperTerminal("Battle");
        this.imagePanel = new ImagePanel();
        setTitle("Battle");
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JPanel jPanelTop = new JPanel();
        jPanelTop.setLayout(new BoxLayout(jPanelTop, BoxLayout.PAGE_AXIS));
        jPanelTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Battle Log"));
        jPanelTop.add(creeperTerminal);
        jPanelTop.setBackground(Color.BLACK);

        add(imagePanel, BorderLayout.CENTER);
        add(jPanelTop, BorderLayout.PAGE_END);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setPreferredSize(new Dimension(400, 720));
        setMinimumSize(new Dimension(400, 720));
        setMaximumSize(new Dimension(400, 720));
        pack();
    }

    @Subscribe
    public void creeperEvent(PlayerData playerData) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (!Strings.isNullOrEmpty(playerData.getActiveFightNpcId())) {
                    Optional<BufferedImage> npcArt = creeperApiHttpClient.getNpcArt(playerData.getActiveFightNpcId());
                    if (npcArt.isPresent()) {
                        imagePanel.setImage(npcArt.get());
                        imagePanel.repaint();
                    }
                }
            }
        });
    }
}
