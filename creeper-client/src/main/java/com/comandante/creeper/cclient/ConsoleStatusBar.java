package com.comandante.creeper.cclient;

import com.comandante.creeper.events.PlayerData;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.world.model.Area;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import com.terminal.ui.ResetEvent;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

import static com.terminal.ui.ColorPane.getTerminalFont;

public class ConsoleStatusBar extends JLabel {
    private final ObjectMapper objectMapper;
    private static final String DEFAULT_TEXT = "";

    public ConsoleStatusBar(ObjectMapper objectMapper) {
        super(DEFAULT_TEXT);
        this.objectMapper = objectMapper;
        setOpaque(true);
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setFont(getTerminalFont());
        setPreferredSize(new Dimension(1, 20));
    }

    private void applyConsoleStatusBarDetails(ConsoleStatusBarDetails consoleStatusBarDetails) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<font color='red'>health: </font>").append(consoleStatusBarDetails.getHealthAmount()).append("/").append(consoleStatusBarDetails.getTotalHealthAmount()).append("&nbsp;&nbsp;");
        sb.append("<font color='#00FFFF'>mana: </font>").append(consoleStatusBarDetails.getManaAmount()).append("/").append(consoleStatusBarDetails.getTotalManaamount()).append("&nbsp;&nbsp;");
        sb.append("<font color='#FFD700'>gold: </font>").append(consoleStatusBarDetails.getGoldAmount()).append("&nbsp;&nbsp;");
        sb.append("<font color='#FF00FF'>xp: </font>").append(consoleStatusBarDetails.getXp()).append("&nbsp;&nbsp;");
        if (consoleStatusBarDetails.getIsFight()) {
            sb.append("<font font-weight: bold; color='red'>[FIGHT] </font>&nbsp;&nbsp;");
        }
        if (consoleStatusBarDetails.getDead()) {
            sb.append("<font font-weight: bold; color='red'>[DEAD] </font>&nbsp;&nbsp;");
        }
        sb.append("<font color='#008000'>" + consoleStatusBarDetails.getCurrentAreas() + "</font>&nbsp;&nbsp;");

        sb.append("</html>");
        setText(sb.toString());
    }

    @Subscribe
    public void resetEvent(ResetEvent resetEvent) {
        reset();
    }

    @Subscribe
    public void creeperEvent(PlayerData playerData) throws IOException {

        PlayerMetadata playerMetadata = playerData.getPlayerMetadata();

        String playerName = playerMetadata.getPlayerName();
        long currentHealth = playerMetadata.getStats().getCurrentHealth();
        long maxHealth = playerData.getPlayerStatsWithLevel().getMaxHealth();
        long currentMana = playerMetadata.getStats().getCurrentMana();
        long maxMana = playerData.getPlayerStatsWithLevel().getMaxMana();
        long gold = playerMetadata.getGold();
        long goldInBankAccount = playerMetadata.getGoldInBank();
        long xp = playerMetadata.getStats().getExperience();
        long currentLevel = playerData.getLevel();
        long xpToNextLevel = playerData.getXpToNextLevel();
        Boolean isInFight = playerData.getInFight();
        java.util.List<String> coolDowns = Lists.newArrayList();


        playerMetadata.getCooldownSet().forEach(coolDown -> {
            if (coolDown.isActive()) {
                coolDowns.add(coolDown.getCoolDownType().toString());
            } else {
                System.out.println("Found inactive cooldown: " + coolDown.getCoolDownType().toString());

            }
        });

        Boolean isDead = false;
        if (coolDowns.contains("DEATH")) {
            isDead = true;
        }

        Set<String> currentAreas = Sets.newHashSet();
        playerData.getCurrentAreas().forEach(new Consumer<Area>() {
            @Override
            public void accept(Area area) {
                currentAreas.add(area.getName());
            }
        });

        StringJoiner stringJoiner = new StringJoiner(",");
        for (String s : currentAreas) {
            stringJoiner.add(s);
        }

        applyConsoleStatusBarDetails(new ConsoleStatusBarDetails(playerName, currentHealth, maxHealth, currentMana, maxMana, Lists.newArrayList(), gold, goldInBankAccount, isInFight, currentLevel, xp, xpToNextLevel, isDead, stringJoiner.toString()));
    }

    private void reset() {
        setText(DEFAULT_TEXT);
    }

}
