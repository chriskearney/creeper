package com.comandante.creeper.command.commands;

import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.model.Player;
import com.google.common.collect.ImmutableList;
import org.fusesource.jansi.Ansi;

import java.util.Set;

import static com.comandante.creeper.model.Color.*;

public class WhoCommand extends Command {

    private final static String helpDescription = "List who you are.";
    public final static ImmutableList validTriggers = new ImmutableList.Builder<String>().add(
            "who".toLowerCase()
    ).build();
    private final static boolean isCaseSensitiveTriggers = false;

    public WhoCommand(String playerId, GameManager gameManager, String originalMessage) {
        super(playerId, gameManager, helpDescription, validTriggers, isCaseSensitiveTriggers, originalMessage);
    }

    @Override
    public void run() {
            Set<Player> allPlayers = getGameManager().getAllPlayers();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(CYAN);
            stringBuilder.append("----------------------\r\n");
            stringBuilder.append("|--active users------|\r\n");
            stringBuilder.append("----------------------\r\n");
            for (Player allPlayer : allPlayers) {
                stringBuilder.append(allPlayer.getPlayerName());
                stringBuilder.append("\r\n");
            }
            stringBuilder.append(new Ansi().reset().toString());
            commandWrite(stringBuilder.toString());
        }
}
