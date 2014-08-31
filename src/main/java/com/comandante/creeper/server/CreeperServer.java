package com.comandante.creeper.server;


import com.comandante.creeper.managers.GameManager;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.mapdb.DB;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class CreeperServer {

    private final int port;
    private final DB db;

    public CreeperServer(int port, DB db) {
        this.port = port;
        this.db = db;
    }

    public void run(GameManager gameManager) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        AuthHandler handler = new AuthHandler(gameManager);
        bootstrap.setPipelineFactory(new CreeperServerPipelineFactory(handler));
        bootstrap.bind(new InetSocketAddress(port));
    }
}
