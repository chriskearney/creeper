package com.comandante.creeper.merchant.bank.commands;


import com.comandante.creeper.common.CreeperEntry;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DoneCommand extends BankCommand {

    final static List<String> validTriggers = Arrays.asList("done");
    final static String description = "Complete transaction.";

    public DoneCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        gameManager.getChannelUtils().write(playerId, "Thanks, COME AGAIN." + "\r\n", true);
        creeperSession.setGrabMerchant(Optional.<CreeperEntry<Merchant, SimpleChannelUpstreamHandler>>empty());
        e.getChannel().getPipeline().remove("executed_command");
        e.getChannel().getPipeline().remove("executed_bank_command");
        String s = gameManager.buildPrompt(playerId);
        write(s);
    }
}
