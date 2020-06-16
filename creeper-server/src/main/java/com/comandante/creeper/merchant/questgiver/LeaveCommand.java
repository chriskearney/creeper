package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.common.CreeperEntry;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class LeaveCommand extends QuestGiverCommand {

    final static List<String> validTriggers = Arrays.asList("leave");
    final static String description = "Leave the discussion.";

    public LeaveCommand(Merchant merchant, GameManager gameManager) {
        super(gameManager, merchant, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        creeperSession.setGrabMerchant(Optional.empty());
        e.getChannel().getPipeline().remove("executed_command");
        e.getChannel().getPipeline().remove(QuestGiverCommand.PIPELINE_NAME);
        String s = gameManager.buildPrompt(playerId);
        write(s);
    }
}
