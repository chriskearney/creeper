package com.comandante.creeper.server.command;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.Area;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class AreaCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("a", "area");
    final static String description = "Alter area settings for the current room.";

    public AreaCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (originalMessageParts.size() == 1) {
                Set<Area> areas = currentRoom.getAreas();
                for (Area area: areas) {
                    write(area.getName() + "\r\n");
                }
                return;
            }
            String s = originalMessageParts.get(1);
            List<String> strings = Arrays.asList(s.split(","));
            Set<Area> newAreas = Sets.newConcurrentHashSet();
            for (String string: strings) {
                String trim = string.trim();
                newAreas.add(Area.getByName(trim));
                write("Added: " + trim + "\r\n");
            }
            currentRoom.setAreas(newAreas);
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
