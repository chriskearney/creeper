package com.comandante.creeper.bot.command;

import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

public class BitlyClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final CreeperConfiguration creeperConfiguration;
    private static final Logger log = Logger.getLogger(BitlyClient.class);

    public BitlyClient(HttpClient httpClient, ObjectMapper objectMapper, CreeperConfiguration creeperConfiguration) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.creeperConfiguration = creeperConfiguration;
    }

    public Optional<ShortenedUrl> shortenUrl(String url) {
        HttpPost httpPost = new HttpPost("https://api-ssl.bitly.com/v4/shorten");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + creeperConfiguration.getBitlyToken());
        try {
            httpPost.setEntity(new StringEntity(objectMapper.writeValueAsString(new BitlyShorten(url, "bit.ly"))));
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");
            HttpResponse execute = httpClient.execute(httpPost);
            JsonNode shortenedResponse = objectMapper.readValue(EntityUtils.toString(execute.getEntity()), JsonNode.class);
            String link = shortenedResponse.get("link").asText();
            String id = shortenedResponse.get("id").asText();
            return Optional.of(new ShortenedUrl(id, link));
        } catch (Exception e) {
            log.error("Unable to shorten link!.", e);
            log.debug("Unable to shorten link: " + url);
        }
        return Optional.empty();
    }

    public Optional<String> getTitle(ShortenedUrl shortenedUrl) {
        HttpGet httpGet = new HttpGet("https://api-ssl.bitly.com/v4/bitlinks/" + shortenedUrl.getId());
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + creeperConfiguration.getBitlyToken());
        httpGet.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");
        try {
            HttpResponse execute = httpClient.execute(httpGet);
            JsonNode shortenedResponse = objectMapper.readValue(EntityUtils.toString(execute.getEntity()), JsonNode.class);
            String title = shortenedResponse.get("title").asText();
            return Optional.of(title);
        } catch (Exception e) {
            log.error("Unable to get shortenedurl!.", e);
        }
        return Optional.empty();
    }

    public Optional<ShortenedUrlAndTitle> getShortenedUrlAndTitle(String longUrl) {
        Optional<ShortenedUrl> shortenedUrl = shortenUrl(longUrl);
        if (!shortenedUrl.isPresent()) {
            return Optional.empty();
        }
        Optional<String> title = getTitle(shortenedUrl.get());
        return Optional.of(new ShortenedUrlAndTitle(shortenedUrl.get().getLink(), title.orElse(null)));
    }

    public static class ShortenedUrlAndTitle {
        private String shortenedUrl;
        private String title;

        public ShortenedUrlAndTitle(String shortenedUrl, String title) {
            this.shortenedUrl = shortenedUrl;
            this.title = title;
        }

        public ShortenedUrlAndTitle() {
        }

        public String getShortenedUrl() {
            return shortenedUrl;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class ShortenedUrl {
        private String id;
        private String link;

        public ShortenedUrl(String id, String link) {
            this.id = id;
            this.link = link;
        }

        public ShortenedUrl() {
        }

        public String getId() {
            return id;
        }

        public String getLink() {
            return link;
        }
    }

    public static class BitlyShorten {
        private String long_url;
        private String domain;

        public BitlyShorten(String long_url, String domain) {
            this.long_url = long_url;
            this.domain = domain;
        }

        public BitlyShorten() {
        }

        public String getLong_url() {
            return long_url;
        }

        public String getDomain() {
            return domain;
        }
    }
}
