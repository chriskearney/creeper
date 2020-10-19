package com.comandante.creeper.bot.command;

import twitter4j.TwitterException;

public interface TwitterAPI {

    TwitterClient.TweetDetails getTweet(String tweetId) throws TwitterException;

}
