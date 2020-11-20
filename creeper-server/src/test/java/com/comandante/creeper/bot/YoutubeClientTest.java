package com.comandante.creeper.bot;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.common.CreeperUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class YoutubeClientTest {

    @Test
    public void testDurationConversion() {
        String pt1H2M30S = YoutubeClient.convertYouTubeDuration("PT1H2M30S");
        System.out.println(pt1H2M30S);
    }



    @Ignore
    @Test
    public void testGetVideoInformation() {

        YoutubeClient youtubeClient = new YoutubeClient(null, Creeper.registerJdkModuleAndGetMapper(), HttpClients.createDefault());
        String oazpaoyacd0 = youtubeClient.getVideoInfo("OAZPAOYACD0");
        System.out.println(oazpaoyacd0);
    }

}