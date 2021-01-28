package com.comandante.creeper.bot;

import com.comandante.creeper.server.player_communication.Color;
import org.devotionit.vantage.AlphaVantageClient;
import org.devotionit.vantage.core.request.QuoteRequest;
import org.devotionit.vantage.core.request.param.Symbol;
import org.devotionit.vantage.core.response.QuoteResponse;
import org.pircbotx.Colors;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class StockPriceManager {
    DecimalFormat currencyFormatter = new DecimalFormat("$#,##0.00;$-#,##0.00");
    NumberFormat numberCommaFormatter = NumberFormat.getInstance();

    private final AlphaVantageClient alphaVantageClient;

    public StockPriceManager(AlphaVantageClient alphaVantageClient) {
        this.alphaVantageClient = alphaVantageClient;
    }

    public String getStockPrice(String symbol) {
        try {
            Stock stock = YahooFinance.get(symbol, true);


                BigDecimal changeInPercent = stock.getQuote().getChangeInPercent();
                BigDecimal change = stock.getQuote().getChange();
                String resp = stock.getQuote().getSymbol() + currencyFormatter.format(stock.getQuote().getPrice());

                if (changeInPercent.compareTo(BigDecimal.ZERO) > 0) {
                    resp += " (" + Colors.GREEN + "\u2191+" + changeInPercent + "%" + Colors.NORMAL + ")";
                    resp += " " + Colors.GREEN + currencyFormatter.format(change) + Colors.NORMAL;
                } else if (changeInPercent.compareTo(BigDecimal.ZERO) < 0) {
                    resp += " (" + Colors.RED + "\u2193+" + changeInPercent + "%" + Colors.NORMAL + ")";
                    resp += " " + Colors.RED + currencyFormatter.format(change) + Colors.NORMAL;
                } else {
                    resp += " (" + changeInPercent + "%)";
                }

                resp += " | open " + currencyFormatter.format(stock.getQuote().getOpen()) +
                        " | high " + currencyFormatter.format(stock.getQuote().getDayHigh()) +
                        " | low " + currencyFormatter.format(stock.getQuote().getDayLow()) + "" +
                        " | volume " + numberCommaFormatter.format(stock.getQuote().getVolume());

            return resp;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
