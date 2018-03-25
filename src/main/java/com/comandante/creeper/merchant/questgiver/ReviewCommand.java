package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.items.ItemMetadata;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.player.Quest;
import com.comandante.creeper.server.ASCIIArt;
import com.comandante.creeper.server.player_communication.Color;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
                write(Color.BOLD_ON + Color.YELLOW + "Quest Name" + Color.RESET + "\r\n");
                write(ASCIIArt.centerOnWidth(quest.getQuestName(), 80, " ") + "\r\n\r\n");
                write(Color.BOLD_ON + Color.YELLOW + "Description" + Color.RESET + "\r\n");
                write(ASCIIArt.wrap("\t" + quest.getQuestDescription() + "\r\n"+ "\r\n"));
                List<Quest.ItemsAmount> requiredItems = quest.getCritera().getItems();
                write(Color.BOLD_ON + Color.YELLOW + "Retrieve" + Color.RESET + "\r\n");
                for (Quest.ItemsAmount itemsAmount: requiredItems) {
                    Optional<ItemMetadata> itemMetadata = gameManager.getItemStorage().get(itemsAmount.getIternalItemName());
                    if (!itemMetadata.isPresent()) {
                        write("ERROR: internal item name is not working: " + itemsAmount.getIternalItemName() + "\r\n");
                        continue;
                    }
                    write(Integer.toString(itemsAmount.getAmount()) + "x " + itemMetadata.get().getItemName() + "\r\n");
                }
                write(Color.BOLD_ON + Color.YELLOW + "\r\nReceive\r\n" + Color.RESET);
                List<Quest.ItemsAmount> rewardItems = quest.getReward().getItems();
                for (Quest.ItemsAmount itemsAmount : rewardItems) {
                    Optional<ItemMetadata> itemMetadata = gameManager.getItemStorage().get(itemsAmount.getIternalItemName());
                    if (!itemMetadata.isPresent()) {
                        write("ERROR: internal item name is not working: " + itemsAmount.getIternalItemName() + "\r\n");
                        continue;
                    }
                    write(Integer.toString(itemsAmount.getAmount()) + "x " + itemMetadata.get().getItemName() + "\r\n");
                }
                write("\r\n");
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