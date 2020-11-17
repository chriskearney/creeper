package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.bot.command.QuoteManager;
import com.google.common.collect.Sets;
import org.pircbotx.Colors;
import org.testng.collections.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        Optional<String> yesFlag = Optional.empty();
        if (!args.isEmpty()) {
            yesFlag = Optional.ofNullable(args.remove(0));
        }
        List<QuoteManager.IrcQuote> byKeyword = botCommandManager.getQuoteManager().grep(keywordQuery);
        if (byKeyword.size() > 100) {
            if (yesFlag.equals("-YES")) {
                botCommandManager.getQuoteProcessor().addIrcQuotes(byKeyword, Optional.ofNullable(getMessageEvent()));
            } else {
                int totalSeconds = byKeyword.size() * 2;
                double minutes = ((double) totalSeconds) / 60;
                return Collections.singletonList("Query will take approximately " + minutes + " minutes. Add -YES to to your command in order to proceed.");
            }
        }
        return Lists.newArrayList();
    }
}