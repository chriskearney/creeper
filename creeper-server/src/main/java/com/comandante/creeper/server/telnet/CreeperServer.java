package com.comandante.creeper.server.telnet;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.server.auth.CreeperAuthenticationHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelException;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class CreeperServer {

    private final int port;

    public CreeperServer(int port) {
        this.port = port;
    }

    public static void exitServer(String exitMessage) {
        exitServer(exitMessage, 0);
    }

    public static void exitServer(String exitMessage, int code) {
        System.out.println("[SERVER IS SHUTTING DOWN] " + exitMessage);
        System.exit(code);
    }

    public void run(GameManager gameManager) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
        CreeperAuthenticationHandler handler = new CreeperAuthenticationHandler(gameManager);
        bootstrap.setPipelineFactory(new CreeperServerPipelineFactory(handler));
        try {
            bootstrap.bind(new InetSocketAddress(port));
        } catch (ChannelException e) {
            exitServer(e.getMessage(), 127);
        }
    }
}
