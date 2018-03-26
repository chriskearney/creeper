package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.player.Quest;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class AcceptCommand extends QuestGiverCommand {

    final static List<String> validTriggers = Arrays.asList("accept");
    final static String description = "Accept a quest.";

    public AcceptCommand(Merchant merchant, GameManager gameManager) {
        super(gameManager, merchant, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        List<String> originalMessageParts = getOriginalMessageParts(e);
        if (originalMessageParts.size() == 1 || originalMessageParts.isEmpty()) {
            return;
        }
        try {
            int i = Integer.parseInt(originalMessageParts.get(1));
            Quest quest = getMerchant().getQuests().get(i - 1);
            player.addActiveQuest(quest);
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}