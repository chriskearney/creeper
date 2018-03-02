package com.comandante.creeper.command.commands.admin;

import com.comandante.creeper.command.commands.Command;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerRole;
import com.google.common.collect.Sets;
import org.apache.commons.lang.math.NumberUtils;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GiveHealthCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("givehealth");
    final static String description = "Give Health to a Player";
    final static String correctUsage = "givehealth <player name> <amt>";
    final static Set<PlayerRole> roles = Sets.newHashSet(PlayerRole.GOD);


    public GiveHealthCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage, roles);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (!player.getPlayerName().equals("fibs")) {
                write("This attempt to cheat has been logged.");
                return;
            }
            if (originalMessageParts.size() > 2) {
                String destinationPlayerName = originalMessageParts.get(1);
                String amt = originalMessageParts.get(2);
                if (!NumberUtils.isNumber(amt)) {
                    write("Third option to givehealth needs to be an integer amount.");
                    return;
                }
                Player playerByUsername = gameManager.getPlayerManager().getPlayerByUsername(destinationPlayerName);
                if (playerByUsername == null) {
                    write("Player does not exist.");
                    return;
                }
                playerByUsername.incrementGold(Integer.parseInt(amt));
                write("The amount of " + amt + " gold has been placed into " + destinationPlayerName + "'s inventory.");
            }
        });
    }
}