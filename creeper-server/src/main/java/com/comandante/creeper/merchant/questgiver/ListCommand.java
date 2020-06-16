package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class ListCommand extends QuestGiverCommand {

    final static List<String> validTriggers = Arrays.asList("list");
    final static String description = "list available quests";

    public ListCommand(Merchant merchant, GameManager gameManager) {
        super(gameManager, merchant, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        try {
            configure(e);
            write(getMerchant().getQuestsIntro(player));
        } finally {
//            e.getChannel().getPipeline().remove("executed_command");
//            e.getChannel().getPipeline().remove(QuestGiverCommand.PIPELINE_NAME);
            super.messageReceived(ctx, e);
        }
    }
}