package com.comandante.telnetserver;


import com.comandante.RoomManager;
import com.comandante.SimpleGameAuthenticator;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class TelnetServer {

    private final int port;

    public TelnetServer(int port) {
        this.port = port;
    }

    public void run(RoomManager roomManager) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        TelnetServerHandler handler = new TelnetServerHandler(new SimpleGameAuthenticator(roomManager), roomManager);
        bootstrap.setPipelineFactory(new TelnetServerPipelineFactory(handler));
        bootstrap.bind(new InetSocketAddress(8080));

    }
}
