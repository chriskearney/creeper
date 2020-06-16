package com.comandante.creeper.bot.command;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;

import java.util.List;
import java.util.Optional;

public class TwitterManager {

    private final TwitterClient twitterClient;

    public TwitterManager(TwitterClient twitterClient) {
        this.twitterClient = twitterClient;
    }

    public Optional<String> parseChatLineToTweetText(String chatLine) {
        try {
            Optional<String> twitterId = extractFirstTwitterUrl(chatLine);
            if (!twitterId.isPresent()) {
                return Optional.empty();
            }
            return Optional.ofNullable(twitterClient.getTweet(twitterId.get()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected static Optional<String> extractFirstTwitterUrl(String chatLine) {
        UrlDetector parser = new UrlDetector(chatLine, UrlDetectorOptions.Default);
        List<Url> found = parser.detect();

        for(Url url : found) {
            if (url.getHost().endsWith("twitter.com")) {
                if (url.getPath().contains("status")) {
                    String[] split = url.getPath().split("/");
                    return Optional.of(split[split.length - 1]);
                }
            }
        }
        return Optional.empty();
    }
}
