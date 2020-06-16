package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.CoolDownType;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FightKillCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("kill", "k", "fight");
    final static String description = "Initiate a fight with a mob.";
    final static String correctUsage = "kill <mob name>";

    public FightKillCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (player.getCurrentHealth() <= 0) {
                write("You have no health and as such you can not attack.");
                return;
            }
            if (player.getActiveFights().size() > 0) {
                write("You are already in a fight!");
                return;
            }
            if (player.isActive(CoolDownType.DEATH)) {
                write("You are dead and can not attack.");
                return;
            }
            if (originalMessageParts.size() == 1) {
                write("You need to specify who you want to fight.");
                return;
            }
            originalMessageParts.remove(0);
            String target = Joiner.on(" ").join(originalMessageParts);
            Set<String> npcIds = currentRoom.getNpcIds();
            for (String npcId : npcIds) {
                Npc npcEntity = entityManager.getNpcEntity(npcId);
                if (npcEntity.getValidTriggers().contains(target)) {
                    if (player.addActiveFight(npcEntity)) {
                        writeToRoom(player.getPlayerName() + " has attacked a " + npcEntity.getColorName());
                       // player.addActiveFight(npcEntity);
                        return;
                    } else {
                        return;
                    }
                }
            }
            write("There's no NPC here to fight by that name.");
        });
    }
}
