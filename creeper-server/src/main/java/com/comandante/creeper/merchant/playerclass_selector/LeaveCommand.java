package com.comandante.creeper.merchant.playerclass_selector;

import com.comandante.creeper.common.CreeperEntry;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LeaveCommand extends PlayerClassCommand {

    final static List<String> validTriggers = Arrays.asList("leave");
    final static String description = "Leave the discussion.";

    public LeaveCommand(GameManager gameManager) {
        super(gameManager);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        creeperSession.setGrabMerchant(Optional.<CreeperEntry<Merchant, SimpleChannelUpstreamHandler>>empty());
        e.getChannel().getPipeline().remove("executed_command");
        e.getChannel().getPipeline().remove("executed_playerclass_command");
        String s = gameManager.buildPrompt(playerId);
        write(s);
    }
}
