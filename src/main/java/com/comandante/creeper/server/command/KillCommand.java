package com.comandante.creeper.server.command;

import com.comandante.creeper.fight.FightManager;
import com.comandante.creeper.fight.FightResults;
import com.comandante.creeper.fight.FightRun;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

public class KillCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("k", "kill");
    final static String description = "Kill a mob.";

    public KillCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            CreeperSession creeperSession = extractCreeperSession(e.getChannel());
            Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId(creeperSession));
            if (FightManager.isActiveFight(creeperSession)) {
                getGameManager().getChannelUtils().write(player.getPlayerId(), "You are already in a fight!");
                return;
            }
            List<String> originalMessageParts = getOriginalMessageParts(e);
            if (originalMessageParts.size() == 1) {
                getGameManager().getChannelUtils().write(player.getPlayerId(), "You need to specify who you want to kill.");
                return;
            }
            originalMessageParts.remove(0);
            String target = Joiner.on(" ").join(originalMessageParts);
            Room playerCurrentRoom = getGameManager().getRoomManager().getPlayerCurrentRoom(player).get();
            Set<String> npcIds = playerCurrentRoom.getNpcIds();
            for (String npcId : npcIds) {
                Npc npcEntity = getGameManager().getEntityManager().getNpcEntity(npcId);
                if (npcEntity.getValidTriggers().contains(target)) {
                    npcEntity.setIsInFight(true);
                    FightRun fightRun = new FightRun(player, npcEntity, getGameManager());
                    Future<FightResults> fight = getGameManager().getFightManager().fight(fightRun);
                    creeperSession.setActiveFight(Optional.of(fight));
                    return;
                }
            }
            getGameManager().getChannelUtils().write(player.getPlayerId(), "There's no NPC here to kill by that name.");
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}
