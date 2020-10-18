package com.comandante.creeper.bot.command;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;

import java.util.List;
import java.util.Optional;

public class BitlyManager {

    private final BitlyClient bitlyClient;

    public BitlyManager(BitlyClient bitlyClient) {
        this.bitlyClient = bitlyClient;
    }

    public Optional<BitlyClient.ShortenedUrlAndTitle> parseChatLineToTweetText(String chatLine) {
        try {
            Optional<String> longUrl = extractFirstUrl(chatLine);
            if (!longUrl.isPresent()) {
                return Optional.empty();
            }
            return bitlyClient.getShortenedUrlAndTitle(longUrl.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected static Optional<String> extractFirstUrl(String chatLine) {
        UrlDetector parser = new UrlDetector(chatLine, UrlDetectorOptions.Default);
        List<Url> found = parser.detect();

        for(Url url : found) {
            return Optional.ofNullable(url.getFullUrl());
        }
        return Optional.empty();
    }
}
