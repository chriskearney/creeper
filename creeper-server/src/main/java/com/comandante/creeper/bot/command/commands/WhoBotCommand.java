package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.stats.DefaultStats;
import com.comandante.creeper.stats.Levels;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class WhoBotCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("who");
    static String helpUsage = "who";
    static String helpDescription = "Who is connected to the mud?";

    public WhoBotCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        Set<Player> allPlayers = botCommandManager.getGameManager().getAllPlayers();
        for (Player player: allPlayers) {
            Optional<PlayerMetadata> playerMetadataOptional = botCommandManager.getGameManager().getPlayerManager().getPlayerMetadata(player.getPlayerId());
            if (!playerMetadataOptional.isPresent()) {
                continue;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            String line = player.getPlayerName() + " (level " + Levels.getLevel(playerMetadata.getStats().getExperience()) + ") - " + player.getCurrentRoom().getRoomTitle();
            resp.add(line);
        }
        return resp;
    }
}
