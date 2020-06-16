package com.comandante.creeper.command.commands;

import com.comandante.creeper.core_game.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class CoolDownCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("cooldown", "cooldowns", "cool");
    final static String description = "Report status of current cooldowns.";
    final static String correctUsage = "cooldowns";

    public CoolDownCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            if (player.isActiveCoolDown()) {
                write(gameManager.renderCoolDownString(player.getCoolDowns()));
            } else {
                write("No active cooldowns.\r\n");
            }
        });

    }
}