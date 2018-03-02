package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.Loot;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcBuilder;
import com.comandante.creeper.player.PlayerRole;
import com.comandante.creeper.server.player_communication.Color;
import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SpawnCommand  extends Command {

    final static List<String> validTriggers = Arrays.asList("spawn");
    final static String description = "Spawn a NPC.";
    final static String correctUsage = "spawn <npc name> | spawn";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);

    public SpawnCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            List<Npc> npcsFromFile = gameManager.getNpcStorage().getAllNpcs();
            if (originalMessageParts.size() == 1) {
                write(getHeader());
                for (Npc npc: npcsFromFile) {
                    write(npc.getName() + "\r\n");
                }
            } else {
                originalMessageParts.remove(0);
                String targetNpc = Joiner.on(" ").join(originalMessageParts);
                for (Npc npc: npcsFromFile) {
                    if (targetNpc.equals(npc.getName())) {
                        Loot loot = new Loot(0,0, Sets.newHashSet());
                        Npc modifiedNpc = new NpcBuilder(npc).setSpawnRules(null).setLoot(loot).createNpc();
                        modifiedNpc.getStats().setExperience(0);
                        modifiedNpc.setCurrentRoom(currentRoom);
                        gameManager.getEntityManager().addEntity(modifiedNpc);
                        currentRoom.addPresentNpc(modifiedNpc.getEntityId());
                        writeToRoom("A " + modifiedNpc.getColorName() + " appears." + "\r\n");
                        return;
                    }
                }
                write("No npc found with name: " + targetNpc + "\r\n");
            }
        });
    }

    public String getHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(Color.MAGENTA + "-+=[ " + Color.RESET).append("Spawn").append(Color.MAGENTA + " ]=+- " + Color.RESET).append("\r\n");
        sb.append(Color.MAGENTA + "AvailableNpcs-----------------------" + Color.RESET).append("\r\n");
        return sb.toString();
    }
}
