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
        if (!botCommandManager.getQuoteProcessor().isEmpty()) {
            return Lists.newArrayList();
        }
        String keywordQuery = args.remove(0);
        Optional<String> yesFlag = Optional.empty();
        if (!args.isEmpty()) {
            yesFlag = Optional.ofNullable(args.remove(0));
        }
        List<QuoteManager.IrcQuote> byKeyword = botCommandManager.getQuoteManager().grep(keywordQuery);
        if (byKeyword.size() > 10) {
            if (yesFlag.isPresent() && yesFlag.get().equals("-YES")) {
                botCommandManager.getQuoteProcessor().addIrcQuotes(byKeyword, Optional.ofNullable(getMessageEvent()));
                return Lists.newArrayList();
            } else {
                int totalSeconds = byKeyword.size() * 2;
                double minutes = ((double) totalSeconds) / 60;
                String matchesFound = "[" + byKeyword.size() + "] results found.";
                return Collections.singletonList(matchesFound + " Query will take approximately " + round(minutes, 2) + " minutes. Add -YES to to your command in order to proceed.");
            }
        }
        botCommandManager.getQuoteProcessor().addIrcQuotes(byKeyword, Optional.ofNullable(getMessageEvent()));
        return Lists.newArrayList();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}