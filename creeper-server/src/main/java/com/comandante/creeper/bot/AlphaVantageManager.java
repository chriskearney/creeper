package com.comandante.creeper.bot;

import org.devotionit.vantage.AlphaVantageClient;
import org.devotionit.vantage.core.request.QuoteRequest;
import org.devotionit.vantage.core.request.param.Symbol;
import org.devotionit.vantage.core.response.QuoteResponse;

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
        CompletableFuture<QuoteResponse> async = alphaVantageClient.getAsync(new QuoteRequest(Symbol.from(symbol)));
        try {
            QuoteResponse quoteResponse = async.get(10L, TimeUnit.SECONDS);
            String quoteDetails = quoteResponse.getSymbol() + "[" +
                    "price " + currencyFormatter.format(quoteResponse.getPrice()) +
                    " | open " + currencyFormatter.format(quoteResponse.getOpen()) +
                    " | high " + currencyFormatter.format(quoteResponse.getHigh()) +
                    " | low " + currencyFormatter.format(quoteResponse.getLow()) + "" +
                    " | volume " + numberCommaFormatter.format(quoteResponse.getVolume()) +
                    " | change " + quoteResponse.getChange() +
                    " | percent " + quoteResponse.getChangePercent() +
                    "]";
            return quoteDetails;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
