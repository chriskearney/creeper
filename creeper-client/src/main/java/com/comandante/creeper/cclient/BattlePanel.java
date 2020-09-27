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

//        this.npcHealthBar.setMaximumSize(new Dimension(240, 20));
//        this.npcHealthBar.setPreferredSize(new Dimension(240, 20));
//        this.npcHealthBar.setMinimumSize(new Dimension(240, 20));

        this.imagePanel.setMaximumSize(new Dimension(240, 240));
        this.imagePanel.setMinimumSize(new Dimension(240, 240));
        this.imagePanel.setPreferredSize(new Dimension(240, 240));
        this.imagePanel.setBackground(Color.BLACK);
        //setTitle("Battle");
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JPanel jpanelTop = new JPanel();
        jpanelTop.setLayout(new BoxLayout(jpanelTop, BoxLayout.PAGE_AXIS));
        jpanelTop.add(imagePanel);
        jpanelTop.add(npcHealthBar);
        jpanelTop.setBackground(Color.BLACK);
        jpanelTop.setMaximumSize(new Dimension(250, 278));
        jpanelTop.setMinimumSize(new Dimension(250, 278));
        jpanelTop.setPreferredSize(new Dimension(250, 278));
        jpanelTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Enemy"));


        JPanel jpanelBottom = new JPanel();
        jpanelBottom.setLayout(new BoxLayout(jpanelBottom, BoxLayout.PAGE_AXIS));
        jpanelBottom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Battle Log"));
        jpanelBottom.add(creeperTerminal);
        jpanelBottom.setBackground(Color.BLACK);
        jpanelBottom.setMaximumSize(new Dimension(250, 300));
        jpanelBottom.setMinimumSize(new Dimension(250, 300));
        jpanelBottom.setPreferredSize(new Dimension(250, 300));

        add(jpanelBottom, BorderLayout.LINE_START);
        add(jpanelTop, BorderLayout.PAGE_END);
       // setDefaultCloseOperation(HIDE_ON_CLOSE);

        setPreferredSize(new Dimension(250, 800));
        setMinimumSize(new Dimension(250, 800));
        setMaximumSize(new Dimension(250, 800));

        //pack();
    }

    @Subscribe
    public void creeperEvent(PlayerData playerData) {
        SwingUtilities.invokeLater(() -> {
            if (!Strings.isNullOrEmpty(playerData.getActiveFightNpcId())) {
                int percentage = (int) ((1 - playerData.getActiveFightNpcHealthPercentage()) * 100);
                npcHealthBar.setValue(percentage);

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
