package com.comandante.telnetserver;

import com.comandante.GameAuthenticator;
import com.comandante.GameManager;
import com.comandante.Movement;
import com.comandante.Player;
import com.comandante.Room;
import com.google.common.base.Optional;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class TelnetServerHandler extends SimpleChannelUpstreamHandler {

    GameAuthenticator gameAuthenticator;
    GameManager gameManager;

    public TelnetServerHandler(GameAuthenticator gameAuthenticator, GameManager gameManager) {
        this.gameAuthenticator = gameAuthenticator;
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
        TelnetServerAuthState telnetServerAuthState = new TelnetServerAuthState();
        telnetServerAuthState.setState(TelnetServerAuthState.State.promptedForUsername);
        ctx.setAttachment(telnetServerAuthState);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws InterruptedException {
        TelnetServerAuthState telnetServerAuthState = (TelnetServerAuthState) ctx.getAttachment();
        if (!telnetServerAuthState.isAuthed()) {
            doAuthentication(ctx, e);
            if (telnetServerAuthState.isAuthed()) {
                currentRoomLogic(ctx, e);
            }
        } else {
            processCommand(ctx, e);
        }

    }

    private void processCommand(ChannelHandlerContext ctx, MessageEvent e) throws InterruptedException {
        String command = (String) e.getMessage();
        TelnetServerAuthState telnetServerAuthState = (TelnetServerAuthState) ctx.getAttachment();
        Player player = gameManager.getPlayer(new Player(telnetServerAuthState.getUsername().get()));
        Room room = gameManager.getPlayerCurrentRoom(player).get();
        if (command.equals("n")) {
            if (room.getNorthId().isPresent()) {
                Movement movement = new Movement(player, room.getRoomId(), room.getNorthId().get(), command);
                gameManager.movePlayer(movement);
                currentRoomLogic(ctx, e);
            } else {
                e.getChannel().write("There's no northern exit.\r\n");
            }
        }
        if (command.equals("s")) {
            if (room.getSouthId().isPresent()) {
                Movement movement = new Movement(player, room.getRoomId(), room.getSouthId().get(), command);
                gameManager.movePlayer(movement);
                currentRoomLogic(ctx, e);
            } else {
                e.getChannel().write("There's no southern exit.\r\n");
            }
        }
        if (command.equals("e")) {
            if (room.getSouthId().isPresent()) {
                Movement movement = new Movement(player, room.getRoomId(), room.getEastId().get(), command);
                gameManager.movePlayer(movement);
                currentRoomLogic(ctx, e);
            } else {
                e.getChannel().write("There's no eastern exit.\r\n");
            }
        }
        if (command.equals("w")) {
            if (room.getSouthId().isPresent()) {
                Movement movement = new Movement(player, room.getRoomId(), room.getWestId().get(), command);
                gameManager.movePlayer(movement);
                currentRoomLogic(ctx, e);
            } else {
                e.getChannel().write("There's no western exit.\r\n");
            }
        }
        if (command.startsWith("say ")) {
            String s = command.replaceFirst("^say ", "");
            gameManager.say(player, s);
        }
        if (command.startsWith("gossip ")) {
            String s = command.replaceFirst("^gossip ", "");
            gameManager.gossip(player, s);
        }
        if (command.isEmpty()) {
            currentRoomLogic(ctx, e);
        }
    }

    private void printExits(Room room, Channel channel) {
        channel.write("-exits: ");
        if (room.getEastId().isPresent()) {
            channel.write("e(ast) ");
        }
        if (room.getNorthId().isPresent()) {
            channel.write("n(orth) ");
        }
        if (room.getSouthId().isPresent()) {
            channel.write("s(outh). ");
        }
        if (room.getWestId().isPresent()) {
            channel.write("w(est). ");
        }
        channel.write("\r\n");
    }

    private void currentRoomLogic(ChannelHandlerContext ctx, MessageEvent e) {
        TelnetServerAuthState telnetServerAuthState = (TelnetServerAuthState) ctx.getAttachment();
        Player player = new Player(telnetServerAuthState.getUsername().get());
        Optional<Room> playerCurrentRoom = gameManager.getPlayerCurrentRoom(player);
        if (!playerCurrentRoom.isPresent()) {
            gameManager.addPlayerToLobby(gameManager.getPlayer(player));
            playerCurrentRoom = gameManager.getPlayerCurrentRoom(player);
        }
        e.getChannel().write(playerCurrentRoom.get().getRoomDescription() + "\r\n");
        for (Player next : playerCurrentRoom.get().getPresentPlayers()) {
            if (next.getPlayerId().equals(new Player(telnetServerAuthState.getUsername().get()).getPlayerId())) {
                continue;
            }
            e.getChannel().write(next.getPlayerName() + " is here.\r\n");
        }
        printExits(playerCurrentRoom.get(), e.getChannel());
    }

    private void doAuthentication(ChannelHandlerContext ctx, MessageEvent e) {
        String message = (String) e.getMessage();
        TelnetServerAuthState telnetServerAuthState = (TelnetServerAuthState) ctx.getAttachment();
        if (telnetServerAuthState.getState().equals(TelnetServerAuthState.State.promptedForUsername)) {
            telnetServerAuthState.setUsername(Optional.of(message));
            telnetServerAuthState.setState(TelnetServerAuthState.State.promptedForPassword);
            e.getChannel().write("password: ");
            return;
        }
        if (telnetServerAuthState.getState().equals(TelnetServerAuthState.State.promptedForPassword)) {
            telnetServerAuthState.setPassword(Optional.of(message));
        }
        boolean b = gameAuthenticator.authenticatePlayer(telnetServerAuthState.getUsername().get(), telnetServerAuthState.getPassword().get(), e.getChannel());
        if (!b) {
            e.getChannel().write("Auth failed.\r\n");
            e.getChannel().write("username: ");
            telnetServerAuthState.setState(TelnetServerAuthState.State.promptedForUsername);
        } else {
            telnetServerAuthState.setAuthed(true);
            telnetServerAuthState.setState(TelnetServerAuthState.State.authed);
            e.getChannel().write("Welcome to bertha.\r\n");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

}
