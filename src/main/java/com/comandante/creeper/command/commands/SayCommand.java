package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.room.Room;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import java.util.Set;

import static com.comandante.creeper.server.Color.RED;
import static com.comandante.creeper.server.Color.RESET;

public class SayCommand extends Command {

    private final static String helpDescription = "Speak to members of your current room";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "say".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public SayCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        Player sourcePlayer = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        String message = getOriginalMessage().replaceFirst("^say ", "");
        Optional<Room> playerCurrentRoomOpt = getGameManager().getRoomManager().getPlayerCurrentRoom(sourcePlayer);
        if (!playerCurrentRoomOpt.isPresent()) {
            throw new RuntimeException("playerCurrentRoom is missing!");
        }
        Room playerCurrentRoom = playerCurrentRoomOpt.get();
        Set<Player> presentPlayers = getGameManager().getPlayerManager().getPresentPlayers(playerCurrentRoom);
        for (Player presentPlayer : presentPlayers) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(RED);
            stringBuilder.append("<").append(sourcePlayer.getPlayerName()).append("> ").append(message);
            stringBuilder.append(RESET);
            if (presentPlayer.getPlayerId().equals(sourcePlayer.getPlayerId())) {
                commandWrite(stringBuilder.toString());
            } else {
                getGameManager().getChannelUtils().writeNoPrompt(presentPlayer.getPlayerId(), stringBuilder.toString());
            }
        }
    }
}
