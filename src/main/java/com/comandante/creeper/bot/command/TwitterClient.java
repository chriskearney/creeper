package com.comandante.creeper.bot.command;

import com.comandante.creeper.dropwizard.CreeperConfiguration;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClient implements TwitterAPI {

    private final CreeperConfiguration creeperConfiguration;
    private final Twitter twitter;

    public TwitterClient(CreeperConfiguration creeperConfiguration) {
        this.creeperConfiguration = creeperConfiguration;
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(false)
                .setOAuthConsumerKey(creeperConfiguration.getTwitterConsumerKey())
                .setOAuthConsumerSecret(creeperConfiguration.getTwitterConsumerSecret())
                .setOAuthAccessToken(creeperConfiguration.getTwitterAccessToken())
                .setOAuthAccessTokenSecret(creeperConfiguration.getTwitterAccessTokenSecret())
                .setTweetModeExtended(true);
        TwitterFactory tf = new TwitterFactory(cb.build());
        this.twitter = tf.getInstance();
    }

    @Override
    public String getTweet(String tweetId) throws TwitterException {
        Status status = twitter.showStatus(Long.parseLong(tweetId));
        return "@" + status.getUser().getScreenName() + ": " + status.getText();
    }
}
