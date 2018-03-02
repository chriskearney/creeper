package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SaveWorldCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("saveworld");
    final static String description = "Saves the current world to disk.";
    final static String correctUsage = "saveworld";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);

    public SaveWorldCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommandThreadSafe(ctx, e, SaveWorldCommand.class, () -> {
            worldExporter.saveWorld();
            write("World saved.");
        });
    }
}
