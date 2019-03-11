package com.comandante.creeper.server;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.comandante.creeper.server.player_communication.GossipCache;
import com.google.api.client.util.Maps;
import org.apache.commons.configuration.MapConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GossipCacheTest {

    GossipCache gossipCache;

    @Before
    public void setUp() throws Exception {
        GameManager mock = mock(GameManager.class);
        HashMap<String, Object> configuration = Maps.newHashMap();
        configuration.put("max.gossip.cache.size", 100);
        CreeperConfiguration creeperConfiguration = new CreeperConfiguration();
        when(mock.getCreeperConfiguration()).thenReturn(creeperConfiguration);
        this.gossipCache = new GossipCache(mock);
    }

    @Test
    public void testRecentGossip() throws Exception {
        for (int i = 0; i < 20; i++) {
            gossipCache.addGossipLine(String.valueOf(i));
        }
        List<String> recent = gossipCache.getRecent(20);
    }

}