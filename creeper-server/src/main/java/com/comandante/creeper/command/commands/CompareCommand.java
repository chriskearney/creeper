package com.comandante.creeper.command.commands;

import com.comandante.creeper.common.CreeperUtils;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.stats.Levels;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CompareCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("compare");
    final static String description = "Compare yourself to another player or npc.";
    final static String correctUsage = "compare <playerName>||<npc>";

    public CompareCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() == 1) {
                printCurrentRoomInformation();
                return;
            }
            originalMessageParts.remove(0);
            String target = Joiner.on(" ").join(originalMessageParts);
            String selfLookStrong = player.getLookString();
            //Players
            Set<Player> presentPlayers = currentRoom.getPresentPlayers();
            for (Player presentPlayer : presentPlayers) {
                if (presentPlayer != null && presentPlayer.getPlayerName().equals(target)) {
                    String targetLookString = presentPlayer.getLookString();
                    write(CreeperUtils.printStringsNextToEachOther(Lists.newArrayList(selfLookStrong, targetLookString), " | ")+ "\r\n");
                    if (!presentPlayer.getPlayerId().equals(playerId)) {
                        channelUtils.write(presentPlayer.getPlayerId(), player.getPlayerName() + " compares themself to you.", true);
                    }
                }
            }

            //NPCS
            Set<String> npcIds = currentRoom.getNpcIds();
            for (String npcId : npcIds) {
                Npc currentNpc = gameManager.getEntityManager().getNpcEntity(npcId);
                if (currentNpc.getValidTriggers().contains(target)) {
                    String npcLookString = gameManager.getLookString(currentNpc, Levels.getLevel(gameManager.getStatsModifierFactory().getStatsModifier(player).getExperience()));
                    write(CreeperUtils.printStringsNextToEachOther(Lists.newArrayList(selfLookStrong, npcLookString)," | ") + "\r\n");
                }
            }
        });
    }
}
