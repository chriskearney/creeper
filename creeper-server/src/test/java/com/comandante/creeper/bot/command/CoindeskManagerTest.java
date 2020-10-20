package com.comandante.creeper.bot.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
@Ignore
public class CoindeskManagerTest {

    private CoindeskManager coindeskManager = new CoindeskManager(new CoindeskClient(new ObjectMapper()));

    @Test
    public void testIntegrationCoindeskManager() throws Exception {
        System.out.println(coindeskManager.getBitCoinPriceinDollars());
    }

}