package com.comandante.creeper.server;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.comandante.creeper.Main;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.merchant.MerchantCommandHandler;
import com.comandante.creeper.server.command.Command;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class CreeperCommandHandler extends SimpleChannelUpstreamHandler {

    private final GameManager gameManager;
    private final Meter commandMeter =  Main.metrics.meter(MetricRegistry.name(CreeperCommandHandler.class, "commands"));

    public CreeperCommandHandler(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        String rootCommand = getRootCommand(e);
        CreeperSession session = (CreeperSession) e.getChannel().getAttachment();
        if (session.getGrabMultiLineInput().isPresent()) {
            e.getChannel().getPipeline().addLast("executed_command", new MultiLineInputHandler(gameManager));
            super.messageReceived(ctx, e);
            return;
        }
        if (session.getGrabMerchant().isPresent()) {
            Merchant merchant = session.getGrabMerchant().get().getKey();
            e.getChannel().getPipeline().addLast("executed_command", new MerchantCommandHandler(gameManager, merchant));
            super.messageReceived(ctx, e);
            return;
        }
        Command commandByTrigger = Main.creeperCommandRegistry.getCommandByTrigger(rootCommand);
        Main.metrics.counter(MetricRegistry.name(CreeperCommandHandler.class, rootCommand + "-cmd")).inc();
        commandMeter.mark();
        e.getChannel().getPipeline().addLast("executed_command", commandByTrigger);
        super.messageReceived(ctx, e);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
        CreeperSession creeperSession = (CreeperSession) e.getChannel().getAttachment();
        gameManager.setPlayerAfk(creeperSession.getUsername().get());
        gameManager.getPlayerManager().removePlayer(creeperSession.getUsername().get());
    }

    private String getRootCommand(MessageEvent e) {
        String origMessage = (String) e.getMessage();
        if (origMessage.contains(" ")) {
            return origMessage.split(" ")[0].toLowerCase();
        } else {
            return origMessage;
        }
    }
}
