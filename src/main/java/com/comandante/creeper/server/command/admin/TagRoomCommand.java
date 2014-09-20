package com.comandante.creeper.server.command.admin;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.server.command.Command;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TagRoomCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("tr", "tagRoom");
    final static String description = "Sets a tag on a world.";
    final static boolean isAdminOnly = true;

    public TagRoomCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, isAdminOnly);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            originalMessageParts.remove(0);
            if (originalMessageParts.get(0).equalsIgnoreCase("list")) {
                StringBuilder sb = new StringBuilder();
                Iterator<String> iterator = currentRoom.getRoomTags().iterator();
                while (iterator.hasNext()) {
                    String tag = iterator.next();
                    sb.append(tag).append("\n");
                }
                write("tag\n---");
                write(sb.toString());
                return;
            }
            currentRoom.addTag(originalMessageParts.get(0));
            write(String.format("tagged world with tag: \"%s\".", originalMessageParts.get(0)));
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
