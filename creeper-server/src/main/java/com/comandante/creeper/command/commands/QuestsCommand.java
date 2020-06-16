package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Quest;
import com.google.common.collect.Sets;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class QuestsCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("quests", "q");
    final static String description = "Manage your quests.";
    final static String correctUsage = "quests";

    public QuestsCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (originalMessageParts.size() == 1) {
                write(getHelp());
                write(player.getQuestsMenu());
                return;
            }
            HashSet<String> validActions = Sets.newHashSet("review", "remove");
            String action = null;
            if (originalMessageParts.size() > 1) {
                action = originalMessageParts.get(1);
                if (validActions.stream().noneMatch(action::equalsIgnoreCase)) {
                    write(getHelp());
                    return;
                }
            }

            Optional<Quest> quest = getQuest();
            if (!quest.isPresent()) {
                write(getHelp());
                return;
            }
            if (action.equalsIgnoreCase("review")) {
                write(player.getQuestReview(quest.get()));
                return;
            }
            if (action.equalsIgnoreCase("remove")) {
                player.removeQuest(quest.get());
                write(quest.get().getQuestName() + " has been removed from your active quests.");
                return;
            }
        });
    }

    private Optional<Quest> getQuest() {
        Integer questNumber = null;
        if (originalMessageParts.size() != 3) {
            return Optional.empty();
        }
        try {
            questNumber = Integer.parseInt(originalMessageParts.get(2));
        } catch (Exception ex) {
            return Optional.empty();
        }
        Quest quest = null;
        try {
            quest = player.getActiveQuests().get(questNumber - 1);
        } catch (Exception ex) {
            write("Invalid quest number.\r\n");
            return Optional.empty();
        }
        return Optional.ofNullable(quest);
    }

    private String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append("You can manipulate quests using the following syntax:\r\n").append("quests review <#> | quests remove <#>").append("\r\n");
        return sb.toString();
    }



}