package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.CreeperEntry;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.server.MultiLineInputManager;
import com.comandante.creeper.server.command.Command;
import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InfoCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("info");
    final static String description = "Get additional info. For admins only.";
    final static boolean isAdminOnly = true;

    public InfoCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            write("roomId: " + currentRoom.getRoomId());
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}