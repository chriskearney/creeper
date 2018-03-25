package com.comandante.creeper.merchant.questgiver;

import com.comandante.creeper.command.commands.CommandAuditLog;
import com.comandante.creeper.configuration.ConfigureCommands;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.merchant.lockers.LockerCommand;
import com.comandante.creeper.merchant.playerclass_selector.PlayerClassCommand;
import com.comandante.creeper.server.model.CreeperSession;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class QuestGiverCommandHandler extends SimpleChannelUpstreamHandler {

    private final GameManager gameManager;
    private final Merchant merchant;

    public QuestGiverCommandHandler(GameManager gameManager, Merchant merchant) {
        this.gameManager = gameManager;
        this.merchant = merchant;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String rootCommand = getRootCommand(e);
        CreeperSession session = (CreeperSession) e.getChannel().getAttachment();
        CommandAuditLog.logCommand(rootCommand, session.getUsername().get());
        QuestGiverCommand commandByTrigger = ConfigureCommands.questGiverCommandRegistry.getCommandByTrigger(merchant, rootCommand);
        QuestGiverCommand cmd = commandByTrigger.createObj(merchant, commandByTrigger.getClass().getName());
        e.getChannel().getPipeline().addLast(QuestGiverCommand.PIPELINE_NAME, cmd);
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
