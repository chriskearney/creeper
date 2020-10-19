package com.comandante.creeper.bot.command;

import com.comandante.creeper.dropwizard.CreeperConfiguration;
import org.apache.log4j.Logger;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TwitterClient implements TwitterAPI {

    private final CreeperConfiguration creeperConfiguration;
    private final Twitter twitter;
    private static final Logger log = Logger.getLogger(TwitterClient.class);

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
    public Optional<TweetDetails> getTweet(String tweetId) throws TwitterException {
        try {
            Status status = twitter.showStatus(Long.parseLong(tweetId));
            List<String> tweetStrings = Arrays.asList(status.getText().split("\\r?\\n"));
            return Optional.of(new TweetDetails(tweetStrings, status.getUser().getScreenName()));
        } catch (Exception exception) {
            log.error("Unable to call twitter api", exception);
        }
        return Optional.empty();
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
