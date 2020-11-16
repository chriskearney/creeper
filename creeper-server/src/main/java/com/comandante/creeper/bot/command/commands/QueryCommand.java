package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.bot.command.QuoteManager;
import com.comandante.creeper.server.player_communication.Color;
import com.google.common.collect.Sets;
import org.testng.collections.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class QueryCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("query", "q");
    static String helpUsage = "query <keyword>";
    static String helpDescription = "Query quotes for a keyword";

    public QueryCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        String keywordQuery = args.remove(0);
        List<QuoteManager.IrcQuote> byKeyword = botCommandManager.getQuoteManager().getByKeyword(keywordQuery);
        List<String> response = Lists.newArrayList();
        int i = 1;
        for (QuoteManager.IrcQuote quote: byKeyword) {
            response.add(Color.BOLD_ON + keywordQuery + "[" + i + "]: " + Color.BOLD_OFF + quote.getQuote());
            i++;
        }
        return response;
    }
}
