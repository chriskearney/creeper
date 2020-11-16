package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.bot.command.QuoteManager;
import com.google.common.collect.Sets;
import org.pircbotx.Colors;
import org.testng.collections.Lists;

import java.util.List;
import java.util.Set;

public class GrepCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("grep");
    static String helpUsage = "grep <keyword>";
    static String helpDescription = "Search quotes by keyword";

    public GrepCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        String keywordQuery = args.remove(0);
        List<QuoteManager.IrcQuote> byKeyword = botCommandManager.getQuoteManager().grep(keywordQuery);
        List<String> response = Lists.newArrayList();
        int i = 1;
        for (QuoteManager.IrcQuote quote: byKeyword) {
            response.add(Colors.BOLD + quote.getKeyword() + "[" + i + "]: " + Colors.BOLD + quote.getQuote());
            i++;
        }
        return response;
    }
}