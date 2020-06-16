package com.comandante.creeper.bot.command;

import twitter4j.TwitterException;

public interface TwitterAPI {

    String getTweet(String tweetId) throws TwitterException;

}
