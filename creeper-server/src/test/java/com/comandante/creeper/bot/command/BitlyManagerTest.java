package com.comandante.creeper.bot.command;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class BitlyManagerTest {

    @Test
    public void testUrl() throws Exception {
        String sampleUrl = "https://www.wbtw.com/georgetown-county-news/family-remembers-georgetown-double-homicide-victims-shot-during-traffic-altercation/";
        Optional<String> s = BitlyManager.extractFirstUrl("asdf asdf as df asdfdasf asdfasdfh nhj htttp as http ;;/// " + sampleUrl);
        Assert.assertEquals(sampleUrl, s.get());
    }

    @Test
    public void testBadUrl() throws Exception {
        String sampleUrl = "Title: creeper/BitlyClient.java at master · chriskearney/creeper · GitHub";
        Optional<String> s = BitlyManager.extractFirstUrl("asdf asdf as df asdfdasf asdfasdfh nhj htttp as http ;;/// " + sampleUrl);
        if (s.isPresent()) {
            Assert.assertFalse(true);
        }
    }
}