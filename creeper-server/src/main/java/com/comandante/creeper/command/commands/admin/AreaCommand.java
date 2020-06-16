package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerRole;
import com.comandante.creeper.world.model.Area;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class AreaCommand extends Command {
    final static List<String> validTriggers = Arrays.asList("area", "a");
    final static String description = "Alter area settings for the current room.";
    final static String correctUsage = "area <area name>,<area name>";
    final static Set<PlayerRole> roles = Sets.newHashSet();

    public AreaCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() == 1) {
                Set<Area> areas = currentRoom.getAreas();
                for (Area area : areas) {
                    write(area.getName() + "\r\n");
                }
                return;
            }
            String s = originalMessageParts.get(1);
            List<String> strings = Arrays.asList(s.split(","));
            Set<Area> newAreas = Sets.newConcurrentHashSet();
            for (String string : strings) {
                String trim = string.trim();
                Area byName = Area.getByName(trim);
                if (byName != null) {
                    newAreas.add(byName);
                    write("added area: " + trim + "\r\n");
                    currentRoom.setAreas(newAreas);
                } else {
                    write(byName + " is not a known area in the code base.");
                }
            }
        });
    }
}
