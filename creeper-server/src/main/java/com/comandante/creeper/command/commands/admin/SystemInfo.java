package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.comandante.creeper.server.player_communication.Color;
import com.google.common.collect.Sets;
import org.apache.commons.io.FileUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SystemInfo extends Command {
    final static List<String> validTriggers = Arrays.asList("sysinfo", "systeminfo", "sys");
    final static String description = "Display System information.";
    final static String correctUsage = "systeminfo";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.ADMIN);

    public SystemInfo(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            write(gameManager.getSystemInfo());
        });
    }
}
