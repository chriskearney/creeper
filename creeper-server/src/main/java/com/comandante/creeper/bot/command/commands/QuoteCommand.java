package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.bot.command.QuoteManager;
import com.google.common.collect.Sets;
import org.testng.collections.Lists;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class QuoteCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("quote", "q");
    static String helpUsage = "quote <symbol>";
    static String helpDescription = "Query stock prices for a symbol";

    public QuoteCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        String stockSymbol = args.remove(0);
        Optional<BigDecimal> amount = Optional.empty();
        try {
            String remove = args.remove(0);
            BigDecimal bigDecimal = new BigDecimal(remove);
            amount = Optional.of(bigDecimal);
        } catch (Exception e) {
            //no-op
        }

        if (amount.isPresent()) {
            BigDecimal calculatedAmount = botCommandManager.getAlphaVantageManager().calculateAmountOfStock(stockSymbol, amount.get());
            String resp = "You have a total of $" + calculatedAmount + " of " + stockSymbol;
            return Collections.singletonList(resp);
        } else {
            String stockPrice = botCommandManager.getAlphaVantageManager().getStockPrice(stockSymbol);
            return Collections.singletonList(stockPrice);
        }
    }
}
