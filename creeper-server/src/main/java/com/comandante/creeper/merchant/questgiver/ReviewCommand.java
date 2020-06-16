package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.player.Quest;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class ReviewCommand extends QuestGiverCommand {

    final static List<String> validTriggers = Arrays.asList("review");
    final static String description = "Review the details of the quest.";

    public ReviewCommand(Merchant merchant, GameManager gameManager) {
        super(gameManager, merchant, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            configure(e);
            List<String> originalMessageParts = getOriginalMessageParts(e);
            if (originalMessageParts.size() == 1 || originalMessageParts.isEmpty()) {
                return;
            }
            try {
                int i = Integer.parseInt(originalMessageParts.get(1));
                Quest quest = getMerchant().getQuests().get(i - 1);
                write(player.getQuestReview(quest));
            } catch (Exception ex) {
                write("\r\n\r\n!! specify a valid quest number.\r\n\r\n");
            }
        } finally {
//            e.getChannel().getPipeline().remove("executed_command");
//            e.getChannel().getPipeline().remove(QuestGiverCommand.PIPELINE_NAME);
            super.messageReceived(ctx, e);
        }
    }
}