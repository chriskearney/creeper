package com.comandante.creeper.bot.command;

import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;

import java.util.List;
import java.util.Optional;

import static com.comandante.creeper.bot.MyListener.extractFirstUrl;

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

    public Optional<BitlyClient.ShortenedUrl> getOnlyBitlyUrl(String chatLine) {
        Optional<String> s = extractFirstUrl(chatLine);
        if (s.isPresent()) {
            return bitlyClient.shortenUrl(s.get());
        }
        return Optional.empty();
    }
}
