package com.comandante.creeper.command.commands;


import com.comandante.creeper.common.CreeperEntry;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.merchant.MerchantCommandHandler;
import com.comandante.creeper.merchant.bank.commands.BankCommand;
import com.comandante.creeper.merchant.lockers.LockerCommand;
import com.comandante.creeper.merchant.playerclass_selector.PlayerClassCommand;
import com.comandante.creeper.merchant.questgiver.QuestGiverCommand;
import com.comandante.creeper.player.PlayerClass;
import com.comandante.creeper.server.player_communication.Color;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class TalkCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("talk");
    final static String description = "Talk to a merchant.";
    final static String correctUsage = "talk <merchant name>";

    public TalkCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        TalkCommand talkCommand = this;
        execCommand(ctx, e, () -> {
            if (creeperSession.getGrabMerchant().isPresent()) {
                creeperSession.setGrabMerchant(Optional.empty());
                return;
            }
            originalMessageParts.remove(0);
            String desiredMerchantTalk = Joiner.on(" ").join(originalMessageParts);
            player.talkMerchant(desiredMerchantTalk, false);
        });
    }
}