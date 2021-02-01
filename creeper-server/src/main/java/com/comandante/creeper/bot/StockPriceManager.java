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
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class StockPriceManager {
    DecimalFormat currencyFormatter = new DecimalFormat("$#,##0.00;$-#,##0.00");
    NumberFormat numberCommaFormatter = NumberFormat.getInstance();

    String downArrow = "\u25BC";
    String upArrow = "\u25B2";

    private final AlphaVantageClient alphaVantageClient;

    public StockPriceManager(AlphaVantageClient alphaVantageClient) {
        this.alphaVantageClient = alphaVantageClient;
    }

    public String calculateAmountOfStock(String symbol, BigDecimal amount, Optional<BigDecimal> costBasis, String nick) {
        try {
            Stock stock = YahooFinance.get(symbol);
            BigDecimal totalValueOfStock = stock.getQuote().getPrice().multiply(amount);
            BigDecimal totalValueOfStockAtCostBasis = BigDecimal.valueOf(0);

            if (costBasis.isPresent()) {
                totalValueOfStockAtCostBasis = costBasis.get().multiply(amount);
            }

            BigDecimal finalResultOfCurrentPrice = totalValueOfStock.setScale(2, RoundingMode.CEILING);

            String response = nick + " has " + currencyFormatter.format(finalResultOfCurrentPrice) + " worth of " + Colors.BOLD + stock.getQuote().getSymbol() + Colors.NORMAL;

            if (costBasis.isPresent()) {
                BigDecimal profit = totalValueOfStock.subtract(totalValueOfStockAtCostBasis);

                BigDecimal percentChange = totalValueOfStock.subtract(totalValueOfStockAtCostBasis).divide(totalValueOfStockAtCostBasis, 2, RoundingMode.CEILING).multiply(BigDecimal.valueOf(100));

                if (percentChange.compareTo(BigDecimal.ZERO) > 0) {
                    response += " (" + Colors.GREEN + upArrow + "+" + percentChange + "%" + Colors.NORMAL + ")";
                    response += " " + Colors.GREEN + upArrow + currencyFormatter.format(profit.setScale(2, RoundingMode.CEILING)) + Colors.NORMAL;
                    response += " " + "basis=" + costBasis.get();
                } else if (percentChange.compareTo(BigDecimal.ZERO) < 0) {
                    response += " (" + Colors.RED + downArrow + percentChange + "%" + Colors.NORMAL + ")";
                    response += " " + Colors.RED + downArrow + currencyFormatter.format(profit.setScale(2, RoundingMode.CEILING)) + Colors.NORMAL;
                    response += " " + "basis=" + costBasis.get();
                } else {
                    response += " (" + percentChange + "%)";
                }
            }

            return response;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getStockPrice(String symbol) {
        try {
            Stock stock = YahooFinance.get(symbol);

            BigDecimal changeInPercent = stock.getQuote().getChangeInPercent();
            BigDecimal change = stock.getQuote().getChange();
            String resp = Colors.BOLD + stock.getQuote().getSymbol() + Colors.NORMAL + " " + currencyFormatter.format(stock.getQuote().getPrice());


            if (changeInPercent.compareTo(BigDecimal.ZERO) > 0) {
                resp += " (" + Colors.GREEN + upArrow + "+" + changeInPercent + "%" + Colors.NORMAL + ")";
                resp += " " + Colors.GREEN + upArrow + currencyFormatter.format(change) + Colors.NORMAL;
            } else if (changeInPercent.compareTo(BigDecimal.ZERO) < 0) {
                resp += " (" + Colors.RED + downArrow + changeInPercent + "%" + Colors.NORMAL + ")";
                resp += " " + Colors.RED + downArrow + currencyFormatter.format(change) + Colors.NORMAL;
            } else {
                resp += " (" + changeInPercent + "%)";
            }

            resp += " [open " + currencyFormatter.format(stock.getQuote().getOpen()) +
                    " | high " + currencyFormatter.format(stock.getQuote().getDayHigh()) +
                    " | low " + currencyFormatter.format(stock.getQuote().getDayLow()) + "" +
                    " | volume " + numberCommaFormatter.format(stock.getQuote().getVolume()) + "]";

            return resp;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
