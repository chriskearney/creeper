package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import com.google.common.collect.ImmutableList;

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
        GameManager gameManager = getGameManager();
        Player player = gameManager.getPlayerManager().getPlayer(getPlayerId());
        getGameManager().say(player, getOriginalMessage().trim().replaceFirst("^say ", ""));
    }
}
