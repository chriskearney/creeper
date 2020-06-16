package com.comandante.creeper.merchant.playerclass_selector;

import com.comandante.creeper.common.CreeperEntry;
import com.comandante.creeper.common.CreeperUtils;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.player.PlayerClass;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.Optional;


public class ChooseClassCommand extends PlayerClassCommand {

    private final PlayerClass playerClass;

    public ChooseClassCommand(PlayerClass playerClass, GameManager gameManager) {
        super(gameManager);
        this.playerClass = playerClass;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        creeperSession.setGrabMerchant(Optional.<CreeperEntry<Merchant, SimpleChannelUpstreamHandler>>empty());
        player.setPlayerClass(playerClass);
        write("You are now and forever, a " + CreeperUtils.capitalize(playerClass.getIdentifier()) + "\r\n");
        e.getChannel().getPipeline().remove("executed_command");
        e.getChannel().getPipeline().remove("executed_playerclass_command");
        String s = gameManager.buildPrompt(playerId);
        write(s);
    }
}