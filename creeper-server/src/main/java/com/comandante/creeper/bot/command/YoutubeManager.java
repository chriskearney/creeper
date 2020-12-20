package com.comandante.creeper.bot.command;

import com.comandante.creeper.bot.YoutubeClient;
import org.apache.log4j.Logger;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.comandante.creeper.bot.MyListener.extractFirstUrl;

public class YoutubeManager {

    private final YoutubeClient youtubeClient;
    private static final Logger log = Logger.getLogger(YoutubeManager.class);


    public YoutubeManager(YoutubeClient youtubeClient) {
        this.youtubeClient = youtubeClient;
    }

    public Optional<String> getVideoIdFromChatLine(String inputLine) {
        Optional<String> firstUrl = extractFirstUrl(inputLine);
        if (!firstUrl.isPresent()) {
            return Optional.empty();
        }
        String videoId = null;
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(firstUrl.get());
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        if (videoId == null) {
            return Optional.empty();
        }
        return Optional.of(videoId);
    }

    public Optional<String> getVideoInfo(String videoId) {
        try {
            String videoInfo = youtubeClient.getVideoInfo(videoId);
            return Optional.of(videoInfo);
        } catch (Exception e) {
            log.error("Unable to get video information.", e);
        }
        return Optional.empty();
    }
}
