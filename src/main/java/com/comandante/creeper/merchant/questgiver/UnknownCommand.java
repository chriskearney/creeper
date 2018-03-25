package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.merchant.playerclass_selector.PlayerClassCommand;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class UnknownCommand extends QuestGiverCommand {

    public UnknownCommand(Merchant merchant, GameManager gameManager) {
        super(gameManager, merchant,null, null);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (getMerchant() != null) {
                write(getMerchant().getWelcomeMessage());
                write(getMerchant().getQuestsMenu());
            }
            write(getPrompt());
            e.getChannel().getPipeline().remove("executed_command");
            e.getChannel().getPipeline().remove(QuestGiverCommand.PIPELINE_NAME);
        } finally {

        }
    }
}
