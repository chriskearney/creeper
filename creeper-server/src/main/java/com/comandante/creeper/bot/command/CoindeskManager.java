package com.comandante.creeper.bot.command;

import java.text.NumberFormat;

public class CoindeskManager {

    private final CoindeskClient coindeskClient;

    public CoindeskManager(CoindeskClient coindeskClient) {
        this.coindeskClient = coindeskClient;
    }

    public String getBitCoinPriceinDollars() {
        double btcPriceDollars = coindeskClient.getBTCPriceDollars();
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(btcPriceDollars);
    }
}
