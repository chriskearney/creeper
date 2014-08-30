package com.comandante.creeper.command.commands;

import com.comandante.creeper.fight.FightManager;
import com.comandante.creeper.fight.FightResults;
import com.comandante.creeper.fight.FightRun;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.room.Room;
import com.comandante.creeper.server.CreeperSession;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Future;

public class KillCommand extends Command {

    private final static String helpDescription = "Kill a NPC.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "kill".toLowerCase(),
            "k".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;
    private final FightManager fightManager;
    private final CreeperSession creeperSession;

    public KillCommand(String playerId, GameManager gameManager, String originalMessage, FightManager fightManager, CreeperSession creeperSession) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
        this.fightManager = fightManager;
        this.creeperSession = creeperSession;
    }

    @Override
    public void run() {

        Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        ArrayList<String> originalMessageParts = getOriginalMessageParts();
        if (originalMessageParts.size() == 1) {
            commandWrite("You need to specify who you want to kill.");
            return;
        }
        originalMessageParts.remove(0);
        String target = Joiner.on(" ").join(originalMessageParts);
        Room playerCurrentRoom = getGameManager().getRoomManager().getPlayerCurrentRoom(player).get();
        Set<String> npcIds = playerCurrentRoom.getNpcIds();
        for (String npcId: npcIds) {
            Npc npcEntity = getGameManager().getEntityManager().getNpcEntity(npcId);
            if (npcEntity.getName().equals(target)) {
                FightRun fightRun = new FightRun(player, npcEntity, getGameManager());
                Future<FightResults> fight = fightManager.fight(fightRun);
                creeperSession.setActiveFight(Optional.of(fight));
                return;
            }
        }
        commandWrite("There's no NPC here to kill by that name.");
    }
}
