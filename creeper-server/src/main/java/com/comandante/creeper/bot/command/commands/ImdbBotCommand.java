package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import com.omertron.omdbapi.OMDBException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImdbBotCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("mov", "movie", "imdb");
    static String helpUsage = "imdb raging bull";
    static String helpDescription = "Get movie information from OMDB.";
    private static final Logger log = Logger.getLogger(ImdbBotCommand.class);

    public ImdbBotCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        String argumentString = joinArgs(args);
        try {
            resp.addAll(botCommandManager.getOmdbManager().getMovieInfo(argumentString));
        } catch (OMDBException e) {
            log.error(e);
        }
        return resp;
    }
}
