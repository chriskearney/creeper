package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class BattlePanel extends JPanel {

    private final CreeperTerminal creeperTerminal;
    private final CreeperApiHttpClient creeperApiHttpClient;
    private final ImagePanel imagePanel;
    private final JProgressBar npcHealthBar;
    private String lastNpcId;

    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BattlePanel.class);

    public BattlePanel(CreeperApiHttpClient creeperApiHttpClient) {
        this.creeperApiHttpClient = creeperApiHttpClient;
        this.creeperTerminal = new CreeperTerminal("Battle");
        this.imagePanel = new ImagePanel();
        this.npcHealthBar = new JProgressBar(0, 100);
        this.npcHealthBar.setValue(100);
        this.npcHealthBar.setStringPainted(true);

        this.npcHealthBar.setMaximumSize(new Dimension(240, 20));
        this.npcHealthBar.setPreferredSize(new Dimension(240, 20));
        this.npcHealthBar.setMinimumSize(new Dimension(240, 20));

        this.imagePanel.setMaximumSize(new Dimension(240, 240));
        this.imagePanel.setMinimumSize(new Dimension(240, 240));
        this.imagePanel.setPreferredSize(new Dimension(240, 240));
        this.imagePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.red)));
        //setTitle("Battle");
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JPanel jpanelTop = new JPanel();
        jpanelTop.setLayout(new BorderLayout());
        jpanelTop.add(imagePanel, BorderLayout.CENTER);
        jpanelTop.add(npcHealthBar, BorderLayout.PAGE_END);
        jpanelTop.setMaximumSize(new Dimension(240, 300));
        jpanelTop.setMinimumSize(new Dimension(240, 300));
        jpanelTop.setPreferredSize(new Dimension(240, 300));
        jpanelTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Enemy"));


        JPanel jpanelBottom = new JPanel();
        jpanelBottom.setLayout(new BoxLayout(jpanelBottom, BoxLayout.PAGE_AXIS));
        jpanelBottom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Battle Log"));
        jpanelBottom.add(creeperTerminal);
        jpanelBottom.setBackground(Color.BLACK);
        jpanelBottom.setMaximumSize(new Dimension(240, 440));
        jpanelBottom.setMinimumSize(new Dimension(240, 440));
        jpanelBottom.setPreferredSize(new Dimension(240, 440));

        add(jpanelTop, BorderLayout.CENTER);
        add(jpanelBottom, BorderLayout.PAGE_END);
       // setDefaultCloseOperation(HIDE_ON_CLOSE);

        setPreferredSize(new Dimension(240, 760));
        setMinimumSize(new Dimension(240, 760));
        setMaximumSize(new Dimension(240, 760));

        //pack();
    }

    @Subscribe
    public void creeperEvent(PlayerData playerData) {
        SwingUtilities.invokeLater(() -> {
            if (!Strings.isNullOrEmpty(playerData.getActiveFightNpcId())) {
                if (lastNpcId != null && lastNpcId.equals(playerData.getActiveFightNpcId())) {
                    return;
                }
                lastNpcId = playerData.getActiveFightNpcId();
                Optional<BufferedImage> npcArt = creeperApiHttpClient.getNpcArt(playerData.getActiveFightNpcId());
                if (npcArt.isPresent()) {
                    try {
                        imagePanel.setImage(resizeImage(npcArt.get(), 240, 240));
                        imagePanel.repaint();
                    } catch (Exception e) {
                        LOG.error("Problem with image", e);
                    }
                }
            }
        });
    }

    BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }
}
