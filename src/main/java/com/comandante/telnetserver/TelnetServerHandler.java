package com.comandante.telnetserver;

import com.comandante.GameAuthenticator;
import com.comandante.Movement;
import com.comandante.Player;
import com.comandante.Room;
import com.comandante.RoomManager;
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
    RoomManager roomManager;

    public TelnetServerHandler(GameAuthenticator gameAuthenticator, RoomManager roomManager) {
        this.gameAuthenticator = gameAuthenticator;
        this.roomManager = roomManager;
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
        if(!telnetServerAuthState.isAuthed()) {
            doAuthentication(ctx, e);
            if (telnetServerAuthState.isAuthed()) {
                currentRoomLogic(ctx, e);
            }
        } else {
            processCommand(ctx, e);
            currentRoomLogic(ctx, e);
        }

    }

    private void processCommand(ChannelHandlerContext ctx, MessageEvent e) throws InterruptedException {
        String command = (String) e.getMessage();
        TelnetServerAuthState telnetServerAuthState = (TelnetServerAuthState) ctx.getAttachment();
        Player player = roomManager.getPlayer(new Player(telnetServerAuthState.getUsername().get()));
        Room room = roomManager.getPlayerCurrentRoom(player).get();
        if (command.equals("n")) {
            if (room.getNorthId().isPresent()) {
                Movement movement = new Movement(player, room.getRoomId(), room.getNorthId().get());
                roomManager._processMovment(movement);
            } else {
                e.getChannel().write("There's no northern exit.\r\n");
            }
        }
        if (command.equals("s")) {
            if (room.getSouthId().isPresent()) {
                Movement movement = new Movement(player, room.getRoomId(), room.getSouthId().get());
                roomManager._processMovment(movement);
            } else {
                e.getChannel().write("There's no southern exit.\r\n");
            }
        }
    }

    private void printExits(Room room, Channel channel) {
        channel.write("Exits: ");
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
        Optional<Room> playerCurrentRoom = roomManager.getPlayerCurrentRoom(player);
        if (!playerCurrentRoom.isPresent()) {
            roomManager.addPlayerToLobby(roomManager.getPlayer(player));
            playerCurrentRoom = roomManager.getPlayerCurrentRoom(player);
        }
        e.getChannel().write(playerCurrentRoom.get().getRoomDescription() + "\r\n");
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
