package com.comandante.server;


import com.comandante.managers.GameManager;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class CreeperServer {

    private final int port;

    public CreeperServer(int port) {
        this.port = port;
    }

    public void run(GameManager gameManager) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        CreeperServerHandler handler = new CreeperServerHandler(new CreeperSimpleAuthenticator(gameManager), gameManager);
        bootstrap.setPipelineFactory(new CreeperServerPipelineFactory(handler));
        bootstrap.bind(new InetSocketAddress(8080));
    }
}
