package com.comandante.creeper.bot.command;

import com.comandante.creeper.dropwizard.CreeperConfiguration;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Arrays;
import java.util.List;

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
    public TweetDetails getTweet(String tweetId) throws TwitterException {
        Status status = twitter.showStatus(Long.parseLong(tweetId));
        List<String> tweetStrings = Arrays.asList(status.getText().split("\\r?\\n"));
        return new TweetDetails(tweetStrings, status.getUser().getScreenName());
    }

    public static class TweetDetails {
        private final List<String> tweetText;
        private final String screeName;

        public TweetDetails(List<String> tweetText, String screeName) {
            this.tweetText = tweetText;
            this.screeName = screeName;
        }

        public List<String> getTweetText() {
            return tweetText;
        }

        public String getScreeName() {
            return screeName;
        }
    }
}
