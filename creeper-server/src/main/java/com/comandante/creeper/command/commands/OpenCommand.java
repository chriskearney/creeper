package com.comandante.creeper.command.commands;

import com.comandante.creeper.common.CreeperEntry;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.merchant.lockers.LockerCommand;
import com.google.common.base.Joiner;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class OpenCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("open", "o");
    final static String description = "Open a locker.";
    final static String correctUsage = "open lockers";

    public OpenCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        OpenCommand openCommand = this;
        execCommand(ctx, e, () -> {
            if (creeperSession.getGrabMerchant().isPresent()) {
                creeperSession.setGrabMerchant(Optional.empty());
                return;
            }
            originalMessageParts.remove(0);
            String desiredMerchantTalk = Joiner.on(" ").join(originalMessageParts);
            Set<Merchant> merchants = currentRoom.getMerchants();
            for (Merchant merchant : merchants) {
                if (merchant.getMerchantType() != Merchant.MerchantType.LOCKER) {
                    return;
                }
                if (merchant.getValidTriggers().contains(desiredMerchantTalk)) {
                    write(merchant.getWelcomeMessage() + "\r\n");
                    write(LockerCommand.getPrompt());
                    creeperSession.setGrabMerchant(Optional.of(new CreeperEntry<>(merchant, openCommand)));
                }
            }
        });
    }
}