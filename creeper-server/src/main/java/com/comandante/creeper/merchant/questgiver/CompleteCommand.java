package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.player.Quest;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class CompleteCommand extends QuestGiverCommand {

    final static List<String> validTriggers = Arrays.asList("complete");
    final static String description = "Complete any available quests.";

    public CompleteCommand(Merchant merchant, GameManager gameManager) {
        super(gameManager, merchant, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            List<Quest> quests = player.questsReadyToTurnIn(getMerchant());
            if (quests.isEmpty()) {
                write("You have no quests that you can complete here.\r\n");
                return;
            }
            player.completeQuest(quests.get(0));
        } finally {
            super.messageReceived(ctx, e);
        }
    }
}