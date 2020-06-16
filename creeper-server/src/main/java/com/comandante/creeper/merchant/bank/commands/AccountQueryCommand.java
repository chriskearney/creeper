package com.comandante.creeper.merchant.bank.commands;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.server.player_communication.Color;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class AccountQueryCommand extends BankCommand {

    final static List<String> validTriggers = Arrays.asList("balance", "query", "q");
    final static String description = "Query account balance.";

    public AccountQueryCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        configure(e);
        try {
            Optional<PlayerMetadata> playerMetadataOptional = playerManager.getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            long goldInBank = playerMetadata.getGoldInBank();
            long gold = playerMetadata.getGold();
            write("You have " + NumberFormat.getNumberInstance(Locale.US).format(goldInBank) + Color.YELLOW + " gold" + Color.RESET + " in your bank account."+ "\r\n");
            write("You have " + NumberFormat.getNumberInstance(Locale.US).format(gold) + Color.YELLOW + " gold" + Color.RESET + " in your inventory."+ "\r\n");
        } finally {
            super.messageReceived(ctx, e);
        }
    }

}
