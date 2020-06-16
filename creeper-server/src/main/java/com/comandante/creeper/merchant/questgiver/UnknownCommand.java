package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

public class UnknownCommand extends QuestGiverCommand {

    public UnknownCommand(Merchant merchant, GameManager gameManager) {
        super(gameManager, merchant,null, null);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
//            if (getMerchant() != null) {
//                write(getMerchant().getWelcomeMessage() + "\r\n");
//                write(getMerchant().getQuestsMenu() + "\r\n");
//            }
            write(getPrompt(getMerchant(), player));
            e.getChannel().getPipeline().remove("executed_command");
            e.getChannel().getPipeline().remove(QuestGiverCommand.PIPELINE_NAME);
        } finally {

        }
    }
}
