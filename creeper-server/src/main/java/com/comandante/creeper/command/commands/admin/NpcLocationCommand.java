package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.PlayerRole;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.world.model.Area;
import com.comandante.creeper.world.model.Room;
import com.google.api.client.util.Maps;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.*;

public class NpcLocationCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("npclocation", "nl");
    final static String description = "Displays location about current NPCS.";
    final static String correctUsage = "npclocation | nl";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.ADMIN);

    public NpcLocationCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            Map<String, Set<Room>> npcMap = Maps.newTreeMap();
            Iterator<Map.Entry<String, Npc>> iterator = entityManager.getNpcs().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Npc> next = iterator.next();
                Npc npc = next.getValue();
                if (npcMap.get(npc.getColorName()) == null) {
                    npcMap.put(npc.getColorName(), Sets.newHashSet(npc.getCurrentRoom()));
                } else {
                    npcMap.get(npc.getColorName()).add(npc.getCurrentRoom());
                }
            }
            Iterator<Map.Entry<String, Set<Room>>> iterator1 = npcMap.entrySet().iterator();
            StringBuilder resp = new StringBuilder();
            while (iterator1.hasNext()) {
                Map.Entry<String, Set<Room>> next = iterator1.next();
                resp.append(next.getKey()).append("\r\n");
                for (Room room : next.getValue()) {
                    if (room != null) {
                        String areas = "";
                        for (Area area : room.getAreas()) {
                            areas = areas + "," + area.getName();
                        }
                        areas = areas.startsWith(",") ? areas.substring(1) : areas;
                        resp.append("    ").append(Color.GREEN + "room name: " + Color.RESET + room.getRoomTitle()).append(" - "+Color.GREEN + "room area: " + Color.RESET + "(").append(areas).append(") - ").append(room.getRoomId());
                    } else {
                        resp.append("    NULL ROOM");
                    }
                    resp.append("\r\n");
                }
            }
            write(resp.toString());
        });
    }
}
