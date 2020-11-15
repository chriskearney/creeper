package com.comandante.creeper.bot;

import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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
            String videoDuration = convertYouTubeDuration(jsonNode.get("items").get(0).get("snippet").get("publishedAt").asText());
            String authorDate = parseYoutubeAuthorDate(jsonNode.get("items").get(0).get("snippet").get("publishedAt").asText());
            String likeCount = jsonNode.get("items").get(0).get("statistics").get("likeCount").asText();
            String dislikeCount = jsonNode.get("items").get(0).get("statistics").get("dislikeCount").asText();
            String channelTitle = jsonNode.get("items").get(0).get("snippet").get("channelTitle").asText();
            String videoViews = jsonNode.get("items").get(0).get("statistics").get("viewCount").asText();
            String videoTitle = jsonNode.get("items").get(0).get("snippet").get("title").asText();
            return videoTitle + " (" + channelTitle + ") | published " + authorDate  + " | duration " + videoDuration + " | views " + putCommas(videoViews) + " | likes " + putCommas(likeCount) + " | dislikes " + putCommas(dislikeCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String putCommas(String source) {
        System.out.println(source);
        return NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(source));
    }

    private String parseYoutubeAuthorDate(String youtubeAuthorDate) {
        ZonedDateTime dateTime = ZonedDateTime.parse(youtubeAuthorDate, DateTimeFormatter.ISO_DATE_TIME);
        return dateTime.getDayOfMonth() + "-" + dateTime.getMonthValue() + "-" + dateTime.getYear();
    }

    private static String buildVideosUrlById(String videoId, String apiKey) {
        return YOUTUBE_BASE_URL + "videos?part=snippet%2CcontentDetails%2Cstatistics&id=" + videoId + "&key=" + apiKey;
    }

    public static String convertYouTubeDuration(String duration) {
        String youtubeDuration = duration; //"PT1H2M30S"; // "PT1M13S";
        Calendar c;
        c = new GregorianCalendar();
        try {
            DateFormat df = new SimpleDateFormat("'PT'mm'M'ss'S'");
            Date d = df.parse(youtubeDuration);
            c.setTime(d);
        } catch (ParseException e) {
            try {
                DateFormat df = new SimpleDateFormat("'PT'hh'H'mm'M'ss'S'");
                Date d = df.parse(youtubeDuration);
                c.setTime(d);
            } catch (ParseException e1) {
                try {
                    DateFormat df = new SimpleDateFormat("'PT'ss'S'");
                    Date d = df.parse(youtubeDuration);
                    c.setTime(d);
                } catch (ParseException e2) {
                }
            }
        }
        c.setTimeZone(TimeZone.getDefault());

        String time = "";
        if ( c.get(Calendar.HOUR) > 0 ) {
            if ( String.valueOf(c.get(Calendar.HOUR)).length() == 1 ) {
                time += "0" + c.get(Calendar.HOUR);
            }
            else {
                time += c.get(Calendar.HOUR);
            }
            time += ":";
        }
        // test minute
        if ( String.valueOf(c.get(Calendar.MINUTE)).length() == 1 ) {
            time += "0" + c.get(Calendar.MINUTE);
        }
        else {
            time += c.get(Calendar.MINUTE);
        }
        time += ":";
        // test second
        if ( String.valueOf(c.get(Calendar.SECOND)).length() == 1 ) {
            time += "0" + c.get(Calendar.SECOND);
        }
        else {
            time += c.get(Calendar.SECOND);
        }
        return time ;
    }
}
