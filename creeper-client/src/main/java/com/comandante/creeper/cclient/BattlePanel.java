package com.comandante.creeper.cclient;

import com.comandante.creeper.events.KillNpcEvent;
import com.comandante.creeper.events.NpcDamageTakenEvent;
import com.comandante.creeper.events.PlayerData;
import com.comandante.creeper.events.PlayerUpdateHealthEvent;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

import static com.comandante.creeper.cclient.CreeperClientMainFrame.RIGHT_SIDE_PANEL_DIMENSIONS;
import static com.comandante.creeper.cclient.CreeperClientMainFrame.RIGHT_SIDE_PANEL_DIMENSIONS_BIGGER;

public class BattlePanel extends JPanel {

    private final CreeperTerminal creeperTerminal;
    private final CreeperApiHttpClient creeperApiHttpClient;
    private final ImagePanel imagePanel;
    private final JProgressBar npcHealthBar;
    private String lastNpcId;
    private final TitledBorder enemyPanelBorder;

    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BattlePanel.class);

    public BattlePanel(CreeperApiHttpClient creeperApiHttpClient, EquipmentPanel equipmentPanel, PlayerInfoPanel playerInfoPanel) {
        this.creeperApiHttpClient = creeperApiHttpClient;
        this.creeperTerminal = new CreeperTerminal("Battle");
        this.imagePanel = new ImagePanel();
        this.npcHealthBar = new JProgressBar(0, 100);
        this.npcHealthBar.setValue(100);
        this.npcHealthBar.setStringPainted(false);

        this.npcHealthBar.setMaximumSize(new Dimension(270, 7));
        this.npcHealthBar.setPreferredSize(new Dimension(270, 7));
        this.npcHealthBar.setMinimumSize(new Dimension(270, 7));

        this.imagePanel.setMaximumSize(new Dimension(270, 240));
        this.imagePanel.setMinimumSize(new Dimension(270, 240));
        this.imagePanel.setPreferredSize(new Dimension(270, 240));
        this.imagePanel.setBackground(Color.BLACK);
        this.enemyPanelBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Enemy");
        //setTitle("Battle");
        setBackground(Color.BLACK);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel jpanelTop = new JPanel();
        jpanelTop.setLayout(new BoxLayout(jpanelTop, BoxLayout.PAGE_AXIS));
        jpanelTop.add(imagePanel);
        jpanelTop.add(npcHealthBar);
        jpanelTop.setBackground(Color.BLACK);
        jpanelTop.setBorder(enemyPanelBorder);

        equipmentPanel.setMaximumSize(new Dimension(270, 138));
        equipmentPanel.setMinimumSize(new Dimension(270, 138));
        equipmentPanel.setPreferredSize(new Dimension(270, 138));
        JPanel jpanelBottom = new JPanel();
        jpanelBottom.setLayout(new BoxLayout(jpanelBottom, BoxLayout.PAGE_AXIS));
        jpanelBottom.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Equipment"));
        jpanelBottom.add(equipmentPanel);
        jpanelBottom.setBackground(Color.BLACK);

        playerInfoPanel.setMaximumSize(new Dimension(270, 138));
        playerInfoPanel.setMinimumSize(new Dimension(270, 138));
        playerInfoPanel.setPreferredSize(new Dimension(270, 138));
        JPanel jpanelPlayerInfo = new JPanel();
        jpanelPlayerInfo.setLayout(new BoxLayout(jpanelPlayerInfo, BoxLayout.PAGE_AXIS));
        jpanelPlayerInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.green), "Player"));
        jpanelPlayerInfo.add(playerInfoPanel);
        jpanelPlayerInfo.setBackground(Color.BLACK);



        //add(equipmentPanel);
        add(jpanelPlayerInfo);
        add(jpanelBottom);
        add(jpanelTop);
        // setDefaultCloseOperation(HIDE_ON_CLOSE);

//        setPreferredSize(new Dimension(250, 800));
//        setMinimumSize(new Dimension(250, 800));
//        setMaximumSize(new Dimension(250, 800));

        //pack();
    }

    @Subscribe
    public void creeperEvent(PlayerData playerData) {
        SwingUtilities.invokeLater(() -> {
            if (playerData.getInFight()) {
                enemyPanelBorder.setBorder(BorderFactory.createLineBorder(Color.red));
                repaint();
            } else {
                enemyPanelBorder.setBorder(BorderFactory.createLineBorder(Color.green));
                repaint();
            }
            if (!Strings.isNullOrEmpty(playerData.getActiveFightNpcId())) {
                if (playerData.getActiveFightNpcHealthPercentage() != null) {
                    npcHealthBar.setValue((int) Math.round(playerData.getActiveFightNpcHealthPercentage()));
                }
                if (lastNpcId != null && lastNpcId.equals(playerData.getActiveFightNpcId())) {
                    return;
                }
                if (lastNpcId != null && lastNpcId != playerData.getActiveFightNpcId()) {
                    creeperTerminal.append("reset");
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
                } else {
                    imagePanel.setImage(null);
                    imagePanel.repaint();
                }
            }
        });
    }

    @Subscribe
    public void creeperEvent(KillNpcEvent killNpcEvent) {
        SwingUtilities.invokeLater(() -> {
            if (killNpcEvent.getNpcId().equals(lastNpcId)) {
                npcHealthBar.setValue(0);
            }
        });
    }

    @Subscribe
    public void creeperEvent(NpcDamageTakenEvent npcDamageTakenEvent) {
        SwingUtilities.invokeLater(() -> {
            if (npcDamageTakenEvent.getNpcId().equals(lastNpcId)) {
                creeperTerminal.append(npcDamageTakenEvent.getDamageAmount() + " damage to " + npcDamageTakenEvent.getColorName());
            }
        });
    }

    @Subscribe
    public void creeperEvent(PlayerUpdateHealthEvent playerUpdateHealthEvent) {
        SwingUtilities.invokeLater(() -> {
            if (playerUpdateHealthEvent.getAmount() == 0) {
                return;
            }
            if (playerUpdateHealthEvent.getAmount() > 0) {
                creeperTerminal.append(playerUpdateHealthEvent.getAmount() + " health to " + playerUpdateHealthEvent.getPlayerName());
            } else {
                creeperTerminal.append(-playerUpdateHealthEvent.getAmount() + " damage to " + playerUpdateHealthEvent.getPlayerName());
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
