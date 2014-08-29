package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;

import static com.comandante.creeper.server.Color.RESET;
import static com.comandante.creeper.server.Color.YELLOW;

public class TellCommand extends Command {

    private final static String helpDescription = "Speak to another player in private.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "tell".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public TellCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
        Player sourcePlayer = getGameManager().getPlayerManager().getPlayer(getPlayerId());
        ArrayList<String> parts = new ArrayList<>(Arrays.asList(getOriginalMessage().split(" ")));
        if (parts.size() < 3) {
            commandWrite("tell failed, no message to send.");
            return;
        }
        //remove the literal 'tell'
        parts.remove(0);
        String destinationUsername = parts.get(0);
        Player desintationPlayer = getGameManager().getPlayerManager().getPlayerByUsername(destinationUsername);
        if (desintationPlayer == null) {
            commandWrite("tell failed, unknown user.");
            return;
        }
        if (desintationPlayer.getPlayerId().equals(sourcePlayer.getPlayerId())) {
            commandWrite("tell failed, you're talking to yourself.");
            return;
        }
        parts.remove(0);
        String tellMessage = Joiner.on(" ").join(parts);
        StringBuilder stringBuilder = new StringBuilder();
        String destinationPlayercolor = YELLOW;
        stringBuilder.append("*").append(sourcePlayer.getPlayerName()).append("* ");
        stringBuilder.append(tellMessage);
        stringBuilder.append(RESET);
        getGameManager().getChannelUtils().writeNoPrompt(desintationPlayer.getPlayerId(), destinationPlayercolor + stringBuilder.toString());
        commandWrite(stringBuilder.toString());
    }
}
