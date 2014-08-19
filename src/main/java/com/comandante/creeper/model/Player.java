package com.comandante.creeper.model;


import org.apache.commons.codec.binary.Base64;
import org.jboss.netty.channel.Channel;

public class Player {

    private String playerName;
    private Channel channel;

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerId() {
        return new String(Base64.encodeBase64(playerName.getBytes()));
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
