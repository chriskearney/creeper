package com.comandante.creeper.server.command;

import com.comandante.creeper.fight.FightManager;
import com.comandante.creeper.fight.FightResults;
import com.comandante.creeper.fight.FightRun;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.npc.Npc;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class FightKillCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("k", "kill", "fight", "f");
    final static String description = "Fight a mob.";

    public FightKillCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (FightManager.isActiveFight(creeperSession)) {
                write("You are already in a fight!");
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
                    npcEntity.setIsInFight(true);
                    FightRun fightRun = new FightRun(player, npcEntity, gameManager);
                    writeToRoom(player.getPlayerName() + " has attacked a " + npcEntity.getColorName());
                    Future<FightResults> fight = fightManager.fight(fightRun);
                    creeperSession.setActiveFight(Optional.of(fight));
                    return;
                }
            }
            write("There's no NPC here to fight by that name.");
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
