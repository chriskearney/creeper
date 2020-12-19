package com.comandante.creeper.bot;

import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.threeten.extra.PeriodDuration;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class YoutubeClient {

    private final HttpClient httpClient;
    private final CreeperConfiguration creeperConfiguration;
    private final ObjectMapper objectMapper;
    private final static String YOUTUBE_BASE_URL = "https://youtube.googleapis.com/youtube/v3/";

    public YoutubeClient(CreeperConfiguration creeperConfiguration, ObjectMapper objectMapper, HttpClient httpClient) {
        this.creeperConfiguration = creeperConfiguration;
        this.objectMapper = objectMapper;
        this.httpClient = httpClient;
    }

    public String getVideoInfo(String videoId) {
        HttpGet httpGet = new HttpGet(buildVideosUrlById(videoId, creeperConfiguration.getYoutubeApi()));
        try {
            HttpResponse execute = httpClient.execute(httpGet);
            String s = EntityUtils.toString(execute.getEntity());
            JsonNode jsonNode = objectMapper.readValue(s, JsonNode.class);
            String videoDuration = convertYouTubeDuration(jsonNode.get("items").get(0).get("contentDetails").get("duration").asText()).replace("**", "");
            String authorDate = parseYoutubeAuthorDate(jsonNode.get("items").get(0).get("snippet").get("publishedAt").asText());
            Optional<String> likeCount = Optional.empty();
            Optional<String> dislikeCount = Optional.empty();
            try {
                likeCount = Optional.ofNullable(putCommas(jsonNode.get("items").get(0).get("statistics").get("likeCount").asText()));
                dislikeCount = Optional.ofNullable(putCommas(jsonNode.get("items").get(0).get("statistics").get("dislikeCount").asText()));
            } catch (Exception ignored) { }
            String channelTitle = jsonNode.get("items").get(0).get("snippet").get("channelTitle").asText();
            String videoViews = jsonNode.get("items").get(0).get("statistics").get("viewCount").asText();
            String videoTitle = jsonNode.get("items").get(0).get("snippet").get("title").asText().replace("\n", "").replace("\r", "");
            return videoTitle + " (" + channelTitle + ") | published " + authorDate  + " | duration " + videoDuration + " | views " + putCommas(videoViews) + " | likes " + likeCount.orElse("n/a") + " | dislikes " + dislikeCount.orElse("n/a");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String putCommas(String source) {
        return NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(source));
    }

    private String parseYoutubeAuthorDate(String youtubeAuthorDate) {
        ZonedDateTime dateTime = ZonedDateTime.parse(youtubeAuthorDate, DateTimeFormatter.ISO_DATE_TIME);
        return dateTime.getMonthValue() + "-" + dateTime.getDayOfMonth() + "-" + dateTime.getYear();
    }

    private static String buildVideosUrlById(String videoId, String apiKey) {
        return YOUTUBE_BASE_URL + "videos?part=snippet%2CcontentDetails%2Cstatistics&id=" + videoId + "&key=" + apiKey;
    }

    public static String convertYouTubeDuration(String duration) {
        PeriodDuration videoDuration = PeriodDuration.parse(duration);
        return DurationFormatUtils.formatDuration(videoDuration.getDuration().toMillis(), "**H:mm:ss**", true);
    }
}
