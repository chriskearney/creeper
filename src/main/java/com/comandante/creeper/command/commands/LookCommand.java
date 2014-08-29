package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.room.Room;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Set;

public class LookCommand extends Command {

    private final static String helpDescription = "Examine an item or other entity.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "look".toLowerCase(),
            "l".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public LookCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        ArrayList<String> originalMessageParts = getOriginalMessageParts();
        if (originalMessageParts.size() == 1) {
            getGameManager().currentRoomLogic(getPlayerId());
            return;
        }
        originalMessageParts.remove(0);
        String target = Joiner.on(" ").join(originalMessageParts);
        //Players
        Player player = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        Room playerCurrentRoom = getGameManager().getRoomManager().getPlayerCurrentRoom(player).get();
        Set<String> presentPlayerIds = playerCurrentRoom.getPresentPlayerIds();
        for (String presentPlayerId: presentPlayerIds) {
            Player presentPlayer = getGameManager().getPlayerManager().getPlayer(presentPlayerId);
            if (presentPlayer.getPlayerName().equals(target)) {
                commandWrite(getGameManager().getPlayerManager().getLookString(player));
            }
        }
    }
}
