package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.common.collect.Sets;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EthCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("eth", "ethereum", "ether");
    static String helpUsage = "eth";
    static String helpDescription = "Get current ethereum price.";

    private final static String ETH_SYMBOL = "eth-usd";

    public EthCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        try {
            Stock stock = YahooFinance.get(ETH_SYMBOL);
            return Collections.singletonList("1 Ether equals " + getCurrencyFormatter().format(stock.getQuote().getPrice()) + " (USD)");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
