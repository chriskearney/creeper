package com.comandante.creeper.bot.command;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

@Ignore
public class BitlyClientTest {

    private BitlyClient bitlyClient;

    @Before
    public void setUp() {
        bitlyClient = new BitlyClient(Creeper.registerJdkModuleAndGetMapper(), new CreeperConfiguration());
    }

    @Test
    public void bitlyIntegrationTest() throws Exception {
        Optional<BitlyClient.ShortenedUrlAndTitle> shortenedUrlAndTitle = bitlyClient.getShortenedUrlAndTitle("https://www.wbtw.com/georgetown-county-news/family-remembers-georgetown-double-homicide-victims-shot-during-traffic-altercation/");
        System.out.println("hi");
    }

    @Test
    public void testGetTitle() throws Exception {
        Optional<String> title = bitlyClient.getTitle("https://old.reddit.com/r/iamatotalpieceofshit/comments/jxgl02/falsifying_results_to_save_money_impacting_how/");
        System.out.println(title.get());
    }
}