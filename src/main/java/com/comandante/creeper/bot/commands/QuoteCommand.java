package com.comandante.creeper.bot.commands;

import com.comandante.creeper.bot.BotCommandManager;
import com.comandante.creeper.bot.Quote;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import com.omertron.omdbapi.OMDBException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class QuoteCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("q", "quote");
    static String helpUsage = "q word";
    static String helpDescription = "Quote things by things..";
    private static final Logger log = Logger.getLogger(QuoteCommand.class);

    public QuoteCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        if (args.size() >= 2) {
            String key = args.get(0);
            args.remove(0);
            String value = joinArgs(args);
            Quote quote = Quote.builder
                    .triggerWord(key)
                    .quote(value)
                    .timestamp(System.currentTimeMillis())
                    .build();



        }
        try {
            resp.addAll(botCommandManager.getOmdbManager().getMovieInfo(argumentString));
        } catch (OMDBException e) {
            log.error(e);
        }
        return resp;
    }
}
