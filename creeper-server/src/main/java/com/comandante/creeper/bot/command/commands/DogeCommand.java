package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.common.collect.Sets;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DogeCommand extends BotCommand {
    static Set<String> triggers = Sets.newHashSet("doge");
    static String helpUsage = "doge";
    static String helpDescription = "Get current dogecoin price.";

    private final static String DOGE_SYMBOL = "doge-usd";

    public DogeCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        try {
            Stock stock = YahooFinance.get(DOGE_SYMBOL);
            return Collections.singletonList("1 Dogecoin equals " + getCurrencyFormatter().format(stock.getQuote().getPrice()) + " (USD)");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
