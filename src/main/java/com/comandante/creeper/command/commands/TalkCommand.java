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
            Set<Merchant> merchants = currentRoom.getMerchants();
            for (Merchant merchant : merchants) {
                if (merchant.getValidTriggers().contains(desiredMerchantTalk)) {
                    if (merchant.getMerchantType() != Merchant.MerchantType.QUESTGIVER) {
                        write(merchant.getWelcomeMessage() + "\r\n");
                    }
                    if (merchant.getMerchantType() == Merchant.MerchantType.BASIC) {
                        write(merchant.getMenu() + "\r\n");
                        gameManager.getChannelUtils().write(playerId, "\r\n" + MerchantCommandHandler.buildPrompt());
                    } else if (merchant.getMerchantType() == Merchant.MerchantType.BANK) {
                        write(BankCommand.getPrompt());
                    } else if (merchant.getMerchantType() == Merchant.MerchantType.LOCKER) {
                        write(LockerCommand.getPrompt());
                    } else if (merchant.getMerchantType() == Merchant.MerchantType.QUESTGIVER) {
                        write(merchant.getQuestsIntro());
                        write(QuestGiverCommand.getPrompt());
                    } else if (merchant.getMerchantType() == Merchant.MerchantType.PLAYERCLASS_SELECTOR) {
                        if (player.getLevel() < 2) {
                            write("Before you can pick a character class, you must be at least level 2.");
                            return;
                        }
                        if (!player.getPlayerClass().equals(PlayerClass.BASIC)) {
                            write("You've already selected a character class. " +
                                    "\r\nIf you'd like to re-select you must present the old wise man with a " +
                                    "" + Color.YELLOW + "golden" + Color.MAGENTA + " fortune cookie" + Color.RESET);
                            return;
                        }
                        write(PlayerClassCommand.getPrompt());
                    }
                    creeperSession.setGrabMerchant(Optional.of(
                            new CreeperEntry<>(merchant, talkCommand)));
                }
            }
        });
    }
}