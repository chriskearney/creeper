package com.comandante.creeper.bot.command;

import org.junit.Test;
import org.testng.Assert;

import java.util.Optional;

import static org.junit.Assert.*;

public class YoutubeManagerTest {

    YoutubeManager youtubeManager = new YoutubeManager(null);

    @Test
    public void testExtractUrl() {
        //https://youtu.be/oaCuiUkvPaA HAHA
        Optional<String> videoIdFromYoutubeUrl = youtubeManager.getVideoIdFromYoutubeUrl("https://youtu.be/oaCuiUkvPaA HAHA");
        Assert.assertEquals("oaCuiUkvPaA", videoIdFromYoutubeUrl.get());
    }

}