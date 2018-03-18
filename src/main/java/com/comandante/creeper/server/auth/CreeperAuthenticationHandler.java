package com.comandante.creeper.server.auth;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.command.CreeperCommandHandler;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SentryManager;
import com.comandante.creeper.server.ServerWelcomeScreen;
import com.comandante.creeper.server.model.CreeperSession;
import com.google.common.base.Optional;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import static com.comandante.creeper.server.player_communication.Color.RESET;

public class CreeperAuthenticationHandler extends SimpleChannelUpstreamHandler {

    private final GameManager gameManager;
    private final CreeperAuthenticator creeperAuthenticator;
    private static final Logger log = Logger.getLogger(CreeperAuthenticationHandler.class);
    public CreeperAuthenticationHandler(GameManager gameManager) {
        this.gameManager = gameManager;
        this.creeperAuthenticator = new GameAuth(gameManager);
    }


    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            System.out.println("Upstream Handling: " + e);
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append(ServerWelcomeScreen.getServerWelcomeScreen())
                .append(RESET + "\r\n")
                .append("First time here? Type \"tupac\".\r\n")
                .append("username: ");
        e.getChannel().write(stringBuilder.toString());
        CreeperSession creeperSession = new CreeperSession();
        creeperSession.setState(CreeperSession.State.promptedForUsername);
        ctx.setAttachment(creeperSession);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        CreeperSession creeperSession = (CreeperSession) ctx.getAttachment();
        if (!creeperSession.isAuthed()) {
            if (creeperSession.state.equals(CreeperSession.State.newUserPromptedForUsername) || creeperSession.state.equals(CreeperSession.State.newUserPromptedForPassword)) {
                gameManager.getNewUserRegistrationManager().handle(creeperSession, e);
                if (!creeperSession.state.equals(CreeperSession.State.newUserRegCompleted)) {
                    return;
                }
            }
            doAuthentication(ctx, e);
            if (creeperSession.isAuthed()) {
                gameManager.getPlayerManager().getSessionManager().putSession(creeperSession);
                e.getChannel().getPipeline().remove(this);
                e.getChannel().getPipeline().addLast("server_handler", new CreeperCommandHandler(gameManager));
                e.getChannel().setAttachment(creeperSession);
                gameManager.getChannelUtils().write(Creeper.createPlayerId(creeperSession.getUsername().get()), "AUTH - " + createEncodedText(creeperSession.getUsername().get(), creeperSession.getPassword().get()));
                gameManager.announceConnect(creeperSession.getUsername().get());
                gameManager.currentRoomLogic(Creeper.createPlayerId(creeperSession.getUsername().get()));
                gameManager.getChannelUtils().write(Creeper.createPlayerId(creeperSession.getUsername().get()), "\r\n" + gameManager.buildPrompt(Creeper.createPlayerId(creeperSession.getUsername().get())));
            }
        } else {
            //gameManager.getPlayerManager().getSessionManager().putSession(creeperSession);
            e.getChannel().getPipeline().addLast("server_handler", new CreeperCommandHandler(gameManager));
            e.getChannel().getPipeline().remove(this);
            e.getChannel().setAttachment(creeperSession);
        }
        super.messageReceived(ctx, e);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        log.error("Authentication failure.", e.getCause());
        SentryManager.logSentry(this.getClass(), e.getCause(), "Authentication failure.");
    }


    private void doAuthentication(ChannelHandlerContext ctx, MessageEvent e) {
        String message = (String) e.getMessage();
        CreeperSession creeperSession = (CreeperSession) ctx.getAttachment();
        if (creeperSession.getState().equals(CreeperSession.State.promptedForUsername)) {
            creeperSession.setUsername(java.util.Optional.of(message.replaceAll("[^a-zA-Z0-9]", "")));
            if (creeperSession.getUsername().isPresent() && creeperSession.getUsername().get().equals("tupac")) {
                gameManager.getNewUserRegistrationManager().newUserRegistrationFlow(creeperSession, e);
                return;
            }
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
            e.getChannel().write("Welcome to creeper. (version: " + Creeper.getCreeperVersion() + ")\r\n");
        }
    }

    private static String createEncodedText(final String username,
                                            final String password) {
        final String pair = username + ":" + password;
        final byte[] encodedBytes = Base64.encodeBase64(pair.getBytes());
        return new String(encodedBytes);
    }

}
