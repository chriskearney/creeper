package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
import com.comandante.creeper.stats.Levels;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ColorPane;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import java.awt.Color;
import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Locale;

public class PlayerInfoPanel extends JPanel {

    private final ColorPane colorPane;

    private String ornateName;
    private String ornateLevelAndClass;
    private final JProgressBar playerManaBar;
    private final JProgressBar playerHealthBar;

    public PlayerInfoPanel() {
        this.colorPane = new ColorPane();
        this.playerManaBar = new JProgressBar(0, 100);
        this.playerManaBar.setValue(100);
        this.playerManaBar.setStringPainted(false);
        this.playerManaBar.setForeground(ColorPane.D_Blue);
        this.playerHealthBar = new JProgressBar(0, 100);
        this.playerHealthBar.setValue(100);
        this.playerHealthBar.setForeground(ColorPane.D_Green);
        this.playerHealthBar.setStringPainted(false);

        this.playerManaBar.setMaximumSize(new Dimension(270, 7));
        this.playerManaBar.setPreferredSize(new Dimension(270, 7));
        this.playerManaBar.setMinimumSize(new Dimension(270, 7));
        this.playerHealthBar.setMaximumSize(new Dimension(270, 7));
        this.playerHealthBar.setPreferredSize(new Dimension(270, 7));
        this.playerHealthBar.setMinimumSize(new Dimension(270, 7));

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(Color.BLACK);
        add(colorPane);
        add(playerManaBar);
        add(playerHealthBar);
        setFocusable(false);
        setVisible(true);
    }

    @Subscribe
    public void creeperEvent(PlayerData playerData) {
        this.ornateName = playerData.getOrnatePlayerName();
        this.ornateLevelAndClass = playerData.getOrnateLevelAndClass();
        long nextLevel = Levels.getLevel(playerData.getPlayerMetadata().getStats().getExperience()) + 1;
        long expToNextLevel = Levels.getXp(nextLevel) - playerData.getPlayerMetadata().getStats().getExperience();
        String gold = "You have " + NumberFormat.getNumberInstance(Locale.US).format(playerData.getPlayerMetadata().getGold()) + com.comandante.creeper.server.player_communication.Color.YELLOW + " gold." + com.comandante.creeper.server.player_communication.Color.RESET;
        String goldInBank = "You have " + NumberFormat.getNumberInstance(Locale.US).format(playerData.getPlayerMetadata().getGoldInBank()) + com.comandante.creeper.server.player_communication.Color.YELLOW + " gold." + com.comandante.creeper.server.player_communication.Color.RESET;
        colorPane.appendANSI(ornateName + "\r\n" + ornateLevelAndClass + "\r\n" + expToNextLevel + " xp to level " + nextLevel + " (" + playerData.getXpRatePerSecondOverOneMinute() + "xp/sec)" + "\r\n" + gold);

        double manaPercent = ((double) playerData.getPlayerMetadata().getStats().getCurrentMana() / playerData.getPlayerStatsWithLevel().getMaxMana()) * 100;
        double healthPercent = ((double) playerData.getPlayerMetadata().getStats().getCurrentHealth() / playerData.getPlayerStatsWithLevel().getMaxHealth()) * 100;

        playerManaBar.setValue((int) Math.round(manaPercent));
        playerHealthBar.setValue((int) Math.round(healthPercent));
    }
}
