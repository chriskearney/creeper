package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.room.WorldExporter;
import com.comandante.creeper.server.command.Command;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class SaveWorldCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("saveworld");
    final static String description = "Saves the current world to disk.";
    final static boolean isAdminOnly = true;


    public SaveWorldCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        WorldExporter worldExporter = new WorldExporter(
                        getGameManager().getRoomManager(),
                        getGameManager().getMapsManager(),
                        getGameManager().getFloorManager(),
                        getGameManager().getEntityManager());

        worldExporter.saveWorld();
        getGameManager().getChannelUtils().write(getPlayerId(extractCreeperSession(e.getChannel())), "World saved.");
        super.messageReceived(ctx, e);
    }
}
