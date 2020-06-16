package com.comandante.creeper.server.player_communication;

import com.comandante.creeper.core_game.GameManager;
import com.google.api.client.util.Lists;
import com.google.common.collect.EvictingQueue;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GossipCache {

    private final GameManager gameManager;
    private final EvictingQueue<String> evictingQueue;

    public GossipCache(GameManager gameManager) {
        this.gameManager = gameManager;
        this.evictingQueue = EvictingQueue.create(gameManager.getCreeperConfiguration().getMaxGossipCacheSize());
    }

    public synchronized void addGossipLine(String line) {
        evictingQueue.add(line);
    }

    public List<String> getRecent(int size) {
        List<String> recent = Lists.newArrayList();
        List<String> currentEntries = evictingQueue.stream().collect(Collectors.toList());

        Collections.reverse(currentEntries);
        int i = 0;
        for (String s : currentEntries) {
            if (i < size) {
                recent.add(s);
                i++;
            } else {
                break;
            }
        }
        Collections.reverse(recent);
        return recent;
    }
}
