package com.comandante.server;

import com.comandante.command.DefaultCommandHandler;
import com.comandante.command.DefaultCommandType;
import com.comandante.managers.GameManager;
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
        // Send greeting for a new connection.
        e.getChannel().write("username: ");
        CreeperSessionState creeperSessionState = new CreeperSessionState();
        creeperSessionState.setState(CreeperSessionState.State.promptedForUsername);
        ctx.setAttachment(creeperSessionState);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws InterruptedException {
        CreeperSessionState creeperSessionState = (CreeperSessionState) ctx.getAttachment();
        if (!creeperSessionState.isAuthed()) {
            doAuthentication(ctx, e);
            if (creeperSessionState.isAuthed()) {
                gameManager.currentRoomLogic(creeperSessionState, e);
            }
        } else {
            DefaultCommandHandler cmdHandler = new DefaultCommandHandler(gameManager, creeperSessionState, e);
            DefaultCommandType cmdType =
                    (DefaultCommandType) DefaultCommandType.getCommandTypeFromMessage(((String) e.getMessage()));
            cmdHandler.handle(cmdType);
        }
    }

    private void doAuthentication(ChannelHandlerContext ctx, MessageEvent e) {
        String message = (String) e.getMessage();
        CreeperSessionState creeperSessionState = (CreeperSessionState) ctx.getAttachment();
        if (creeperSessionState.getState().equals(CreeperSessionState.State.promptedForUsername)) {
            creeperSessionState.setUsername(Optional.of(message));
            creeperSessionState.setState(CreeperSessionState.State.promptedForPassword);
            e.getChannel().write("password: ");
            return;
        }
        if (creeperSessionState.getState().equals(CreeperSessionState.State.promptedForPassword)) {
            creeperSessionState.setPassword(Optional.of(message));
        }
        boolean b = creeperAuthenticator.authenticateAndRegisterPlayer(creeperSessionState.getUsername().get(), creeperSessionState.getPassword().get(), e.getChannel());
        if (!b) {
            e.getChannel().write("Auth failed.\r\n");
            e.getChannel().write("username: ");
            creeperSessionState.setState(CreeperSessionState.State.promptedForUsername);
        } else {
            creeperSessionState.setAuthed(true);
            creeperSessionState.setState(CreeperSessionState.State.authed);
            e.getChannel().write("Welcome to bertha.\r\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

}
