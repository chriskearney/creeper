package com.comandante.creeper.bot;

import org.devotionit.vantage.AlphaVantageClient;
import org.devotionit.vantage.core.request.QuoteRequest;
import org.devotionit.vantage.core.request.param.Symbol;
import org.devotionit.vantage.core.response.QuoteResponse;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AlphaVantageManager {
    DecimalFormat currencyFormatter = new DecimalFormat("$#,##0.00;$-#,##0.00");
    NumberFormat numberCommaFormatter = NumberFormat.getInstance();

    private final AlphaVantageClient alphaVantageClient;

    public AlphaVantageManager(AlphaVantageClient alphaVantageClient) {
        this.alphaVantageClient = alphaVantageClient;
    }

    public String getStockPrice(String symbol) {
        try {
            Stock stock = YahooFinance.get(symbol, true);
            String quoteDetails = stock.getQuote().getSymbol() + "[" +
                    "price " + currencyFormatter.format(stock.getQuote().getPrice()) +
                    " | open " + currencyFormatter.format(stock.getQuote().getOpen()) +
                    " | high " + currencyFormatter.format(stock.getQuote().getDayHigh()) +
                    " | low " + currencyFormatter.format(stock.getQuote().getDayLow()) + "" +
                    " | volume " + numberCommaFormatter.format(stock.getQuote().getVolume()) +
                    " | change " + stock.getQuote().getChange() +
                    " | percent " + stock.getQuote().getChangeInPercent() +
                    "]";
            return quoteDetails;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
