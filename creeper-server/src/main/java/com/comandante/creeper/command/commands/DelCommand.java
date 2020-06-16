package com.comandante.creeper.command.commands;


import com.comandante.creeper.core_game.GameManager;
import com.google.api.client.util.Lists;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DelCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("del");
    final static String description = "Delete a user preference";
    final static String correctUsage = "del || del auto_map";

    public DelCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() <= 1) {
                write(returnAllSettings());
                return;
            }
            originalMessageParts.remove(0);
            String desiredSettingName = originalMessageParts.get(0);
            player.removePlayerSetting(desiredSettingName);
            write("Setting removed.\r\n");
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
}