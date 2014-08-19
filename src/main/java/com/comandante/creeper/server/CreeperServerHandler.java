package com.comandante.creeper.server;

import com.comandante.creeper.command.DefaultCommandHandler;
import com.comandante.creeper.command.DefaultCommandType;
import com.comandante.creeper.managers.GameManager;
import com.google.common.base.Optional;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class CreeperServerHandler extends SimpleChannelUpstreamHandler {

    CreeperAuthenticator creeperAuthenticator;
    GameManager gameManager;

    public CreeperServerHandler(CreeperAuthenticator creeperAuthenticator, GameManager gameManager) {
        this.creeperAuthenticator = creeperAuthenticator;
        this.gameManager = gameManager;
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            System.err.println(e);
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        e.getChannel().write("\r\n\r\n\r\n\r\n" + GameManager.LOGO + "\r\n" + GameManager.VERSION + "\r\n\r\n\r\n\r\n");
        e.getChannel().write("username: ");
        CreeperSession creeperSession = new CreeperSession();
        creeperSession.setState(CreeperSession.State.promptedForUsername);
        ctx.setAttachment(creeperSession);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws InterruptedException {
        CreeperSession creeperSession = (CreeperSession) ctx.getAttachment();
        if (!creeperSession.isAuthed()) {
            doAuthentication(ctx, e);
            if (creeperSession.isAuthed()) {
                gameManager.currentRoomLogic(creeperSession, e);
            }
        } else {
            DefaultCommandHandler cmdHandler = new DefaultCommandHandler(gameManager, creeperSession, e);
            DefaultCommandType cmdType =
                    (DefaultCommandType) DefaultCommandType.getCommandTypeFromMessage(((String) e.getMessage()));
            cmdHandler.handle(cmdType);
        }
    }

    private void doAuthentication(ChannelHandlerContext ctx, MessageEvent e) {
        String message = (String) e.getMessage();
        CreeperSession creeperSession = (CreeperSession) ctx.getAttachment();
        if (creeperSession.getState().equals(CreeperSession.State.promptedForUsername)) {
            creeperSession.setUsername(Optional.of(message));
            creeperSession.setState(CreeperSession.State.promptedForPassword);
            e.getChannel().write("password: ");
            return;
        }
        if (creeperSession.getState().equals(CreeperSession.State.promptedForPassword)) {
            creeperSession.setPassword(Optional.of(message));
        }
        boolean b = creeperAuthenticator.authenticateAndRegisterPlayer(creeperSession.getUsername().get(), creeperSession.getPassword().get(), e.getChannel());
        if (!b) {
            e.getChannel().write("authentication failed.\r\n");
            e.getChannel().write("username: ");
            creeperSession.setState(CreeperSession.State.promptedForUsername);
        } else {
            creeperSession.setAuthed(true);
            creeperSession.setState(CreeperSession.State.authed);
            e.getChannel().write("Welcome back.\r\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

}
