package com.comandante.creeper.bot;

public class IrcUser {


    private final String username;
    private final String password;
    private final String hostmask;

    public IrcUser(String username, String password, String hostmask) {
        this.username = username;
        this.password = password;
        this.hostmask = hostmask;
    }


}
