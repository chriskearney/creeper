package com.comadante.creeper.cclient;

import com.fasterxml.jackson.databind.JsonNode;
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
    public void creeperEvent(CreeperEvent creeperEvent) throws IOException {
        if (!creeperEvent.getCreeperEventType().equals(CreeperEventType.PLAYERDATA)) {
            return;
        }
        JsonNode jsonNode = objectMapper.readValue(creeperEvent.getPayload(), JsonNode.class);
        String playerName = jsonNode.get("playerMetadata").get("playerName").asText();
        long currentHealth = jsonNode.get("playerMetadata").get("stats").get("currentHealth").asLong();
        long maxHealth = jsonNode.get("playerStatsWithLevel").get("maxHealth").asLong();
        long currentMana = jsonNode.get("playerMetadata").get("stats").get("currentMana").asLong();
        long maxMana = jsonNode.get("playerStatsWithLevel").get("maxMana").asLong();
        long gold = jsonNode.get("playerMetadata").get("gold").asLong();
        long goldInBankAccount = jsonNode.get("playerMetadata").get("goldInBank").asLong();
        long xp = jsonNode.get("playerMetadata").get("stats").get("experience").asLong();
        long currentLevel = jsonNode.get("level").asLong();
        long xpToNextLevel = jsonNode.get("xpToNextLevel").asLong();
        Boolean isInFight = jsonNode.get("inFight").asBoolean();
        java.util.List<String> coolDowns = Lists.newArrayList();
        if (jsonNode.get("playerMetadata").has("coolDownMap")) {
            jsonNode.get("playerMetadata").get("coolDownMap").forEach(j -> {
                if (j.get("active").asBoolean()) {
                    coolDowns.add(j.get("coolDownType").asText());
                } else {
                    System.out.println("Found inactive cooldown: " + j.get("coolDownType").asText());
                }
            });
        }
        Boolean isDead = false;
        if (coolDowns.contains("DEATH")) {
            isDead = true;
        }

        Set<String> currentAreas = Sets.newHashSet();
        jsonNode.get("currentAreas").forEach(jsonNode1 -> currentAreas.add(jsonNode1.asText()));
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
