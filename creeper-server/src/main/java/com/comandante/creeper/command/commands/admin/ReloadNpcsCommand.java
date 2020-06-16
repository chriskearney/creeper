package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.configuration.ConfigureNpc;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ReloadNpcsCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("reloadnpcs");
    final static String description = "Reload npcs from disk.";
    final static String correctUsage = "reloadnpcs";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);

    public ReloadNpcsCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommandThreadSafe(ctx, e, ReloadNpcsCommand.class, () -> {
            gameManager.removeAllNpcs();
            try {
                ConfigureNpc.configureAllNpcs(gameManager);
            } catch (IOException ex) {
                log.error("Unable to configure NPCS from disk.");
            }
        });
    }
}