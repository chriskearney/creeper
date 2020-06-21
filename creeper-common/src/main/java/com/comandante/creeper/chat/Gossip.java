package com.comandante.creeper.chat;

import java.util.Map;

public class Gossip {

    private final Map<String, String> userMap;
    private final String message;
    private final String name;
    private final String topic;
    private final long timestamp;

    public Gossip(Map<String, String> userMap, String message, String name, String topic, long timestamp) {
        this.userMap = userMap;
        this.message = message;
        this.name = name;
        this.topic = topic;
        this.timestamp = timestamp;
    }

    public Map<String, String> getUserMap() {
        return userMap;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public String getTopic() {
        return topic;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
