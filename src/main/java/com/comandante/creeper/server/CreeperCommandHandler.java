package com.comandante.creeper.server;

import com.comandante.creeper.Main;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.server.command.Command;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class CreeperCommandHandler extends SimpleChannelUpstreamHandler {

    private final GameManager gameManager;

    public CreeperCommandHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String rootCommand = getRootCommand(e);
        Command commandByTrigger = Main.creeperCommandRegistry.getCommandByTrigger(rootCommand);
        e.getChannel().getPipeline().addLast("executed_command", commandByTrigger);
        super.messageReceived(ctx, e);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
        CreeperSession creeperSession = (CreeperSession) ctx.getAttachment();
        gameManager.setPlayerAfk(creeperSession.getUsername().get());
        gameManager.getPlayerManager().removePlayer(creeperSession.getUsername().get());
    }

    private String getRootCommand(MessageEvent e){
        String origMessage = (String) e.getMessage();
        return origMessage.split(" ")[0].toLowerCase();
    }

}
