package com.comandante.creeper.command.commands;


import com.comandante.creeper.core_game.GameManager;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class ToggleChatCommand extends Command {

    public final static String TOGGLE_CHAT_COMMAND_TRIGGER = "cm";
    final static List<String> validTriggers = Arrays.asList(TOGGLE_CHAT_COMMAND_TRIGGER);
    final static String description = "Configure chat mode.";
    final static String correctUsage = "cm on || /cm off";

    public ToggleChatCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            boolean chat = player.toggleChat();
            if (chat) {
                write("Chat mode enabled.");
            } else {
                write("Chat mode disabled.");
            }
        });
    }

}
