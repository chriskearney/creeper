package com.comandante.server;

import org.jboss.netty.channel.Channel;

public interface CreeperAuthenticator {

    public boolean authenticateAndRegisterPlayer(String userName, String passWord, Channel channel);

}
