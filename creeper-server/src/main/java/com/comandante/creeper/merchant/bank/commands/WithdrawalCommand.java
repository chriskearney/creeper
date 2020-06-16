package com.comandante.creeper.merchant.bank.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.server.player_communication.Color;
import org.apache.commons.lang.math.NumberUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class WithdrawalCommand extends BankCommand {

    final static List<String> validTriggers = Arrays.asList("withdrawal", "get", "w");
    final static String description = "Withdrawal gold.";

    public WithdrawalCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            if (originalMessageParts.size() > 1 && NumberUtils.isNumber(originalMessageParts.get(1))) {
                int withdrawalAmount = Integer.parseInt(originalMessageParts.get(1));
                if (areBankFundsAvailable(withdrawalAmount)) {
                    player.transferBankGoldToPlayer(withdrawalAmount);
                    write("Your funds of " + NumberFormat.getNumberInstance(Locale.US).format(withdrawalAmount) + Color.YELLOW + " gold " + Color.RESET + "have been withdrawn from your bank account." + "\r\n");
                } else {
                    write("You don't have enough" + Color.YELLOW + " gold "+ Color.RESET + "to cover the withdrawal amount." + "\r\n");
                }
            }
        } finally {
            super.messageReceived(ctx, e);
        }
    }

    private boolean areBankFundsAvailable(int amt) {
        Optional<PlayerMetadata> playerMetadataOptional = playerManager.getPlayerMetadata(playerId);
        if (!playerMetadataOptional.isPresent()) {
            return false;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        long bankGold = playerMetadata.getGoldInBank();
        return (bankGold >= amt);
    }

}