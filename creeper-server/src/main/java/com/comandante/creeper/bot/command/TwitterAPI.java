package com.comandante.creeper.bot.command;

import twitter4j.TwitterException;

import java.util.Optional;

public interface TwitterAPI {

    Optional<TwitterClient.TweetDetails> getTweet(String tweetId) throws TwitterException;

}
