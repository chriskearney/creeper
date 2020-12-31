package com.comandante.creeper.bot;

import org.devotionit.vantage.AlphaVantageClient;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class AlphaVantageManagerTest {


    @Test
    @Ignore
    public void testVantage() throws Exception {

        AlphaVantageClient alphaVantageClient = new AlphaVantageClient("N/A");
        AlphaVantageManager alphaVantageManager = new AlphaVantageManager(alphaVantageClient);
        String gme = alphaVantageManager.getStockPrice("gme");
        System.out.println(gme);

    }

}