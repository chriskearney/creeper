package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerSettings;
import com.google.api.client.util.Lists;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class SetCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("set");
    final static String description = "Set a user preference";
    final static String correctUsage = "set || set auto_map 5";

    public SetCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            write("'set help' for full settings help.\r\n");
            if (originalMessageParts.size() <= 1) {
                write(returnAllSettings());
                return;
            }
            if (originalMessageParts.size() > 1 && originalMessageParts.get(1).equalsIgnoreCase("help")) {
                write(returnAllSettingsHelp());
                return;
            }
            originalMessageParts.remove(0);
            String desiredSettingName = originalMessageParts.get(0);
            originalMessageParts.remove(0);
            String desiredSettingValue = Joiner.on(" ").join(originalMessageParts);
            boolean b = player.setPlayerSetting(desiredSettingName, desiredSettingValue);
            if (b) {
                write("Setting successfully set.\r\n");
            } else {
                write ("Unknown Setting.\r\n");
            }
        });
    }

    private String returnAllSettings() {
        Map<String, String> playerSettings = player.getPlayerSettings();
        List<String> settings = Lists.newArrayList();
        for (Map.Entry<String, String> next : playerSettings.entrySet()) {
            String key = next.getKey();
            String value = next.getValue();
            settings.add(key + " : " + value);
        }
        Collections.sort(settings);
        StringBuilder sb = new StringBuilder();
        for (String s: settings) {
            sb.append(s).append("\r\n");
        }
        return sb.toString();
    }

    private String returnAllSettingsHelp() {
        List<String> settings = Lists.newArrayList();
        for (PlayerSettings playerSettings : PlayerSettings.values()) {
            settings.add(playerSettings.getSettingName() + " - " + playerSettings.getSettingDescription());
        }
        Collections.sort(settings);
        StringBuilder sb = new StringBuilder();
        for (String s: settings) {
            sb.append(s).append("\r\n");
        }
        return sb.toString();
    }
}