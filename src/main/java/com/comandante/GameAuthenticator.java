package com.comandante;

import org.jboss.netty.channel.Channel;

public interface GameAuthenticator {

    public boolean authenticatePlayer(String userName, String passWord, Channel channel);

}
