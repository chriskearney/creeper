package com.comandante.creeper.merchant.playerclass_selector;

import com.comandante.creeper.command.commands.CommandAuditLog;
import com.comandante.creeper.configuration.ConfigureCommands;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.server.model.CreeperSession;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class PlayerClassCommandHandler extends SimpleChannelUpstreamHandler {

    private final GameManager gameManager;
    private final Merchant merchant;

    public PlayerClassCommandHandler(GameManager gameManager, Merchant merchant) {
        this.gameManager = gameManager;
        this.merchant = merchant;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String rootCommand = getRootCommand(e);
        CreeperSession session = (CreeperSession) e.getChannel().getAttachment();
        CommandAuditLog.logCommand(rootCommand, session.getUsername().get());
        PlayerClassCommand commandByTrigger = ConfigureCommands.playerClassCommandRegistry.getCommandByTrigger(rootCommand);
        e.getChannel().getPipeline().addLast("executed_playerclass_command", commandByTrigger);
        super.messageReceived(ctx, e);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
        CreeperSession creeperSession = (CreeperSession) e.getChannel().getAttachment();
        gameManager.getPlayerManager().removePlayer(creeperSession.getUsername().get());
    }

    private String getRootCommand(MessageEvent e) {
        String origMessage = (String) e.getMessage();
        String[] split = origMessage.split(" ");
        if (split.length > 0) {
            return split[0].toLowerCase();
        } else {
            return origMessage;
        }
    }
}