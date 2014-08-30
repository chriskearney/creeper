package com.comandante.creeper.player;


import com.comandante.creeper.Main;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.entity.CreeperEntity;
import com.google.common.base.Optional;
import org.apache.commons.codec.binary.Base64;
import org.jboss.netty.channel.Channel;

public class Player extends CreeperEntity {

    private String playerName;
    private Channel channel;
    private Optional<String> returnDirection = Optional.absent();
    private final GameManager gameManager;

    public Player(String playerName, GameManager gameManager) {
        this.playerName = playerName;
        this.gameManager = gameManager;
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

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Optional<String> getReturnDirection() {
        return returnDirection;
    }

    public void setReturnDirection(Optional<String> returnDirection) {
        this.returnDirection = returnDirection;
    }

    @Override
    public void run() {

        if (gameManager.getPlayerManager().getPlayerMetadata(Main.createPlayerId(playerName)).getStats().getCurrentHealth() < 100) {
            gameManager.getPlayerManager().updatePlayerHealth(Main.createPlayerId(playerName), 10);
        }

    }
}
