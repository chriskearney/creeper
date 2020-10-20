package com.comandante.creeper.bot.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.util.Date;

public class CoindeskClient {

    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private static final Logger LOG = Logger.getLogger(CoindeskClient.class);

    public CoindeskClient(HttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public double getBTCPriceDollars() {
        HttpGet httpGet = new HttpGet("https://api.coindesk.com/v1/bpi/currentprice.json");
        try {
            HttpResponse execute = client.execute(httpGet);
            String rawResponse = EntityUtils.toString(execute.getEntity());
            Root response = objectMapper.readValue(rawResponse, Root.class);
            return response.bpi.uSD.rate_float;
        } catch (Exception e) {
            LOG.error("Unable to get current price from coindesk.", e);
        }
        return 0;
    }

    public static class Time {
        public String updated;
        public Date updatedISO;
        public String updateduk;
    }

    public static class USD {
        public String code;
        public String symbol;
        public String rate;
        public String description;
        public double rate_float;
    }

    public static class GBP {
        public String code;
        public String symbol;
        public String rate;
        public String description;
        public double rate_float;
    }

    public static class EUR {
        public String code;
        public String symbol;
        public String rate;
        public String description;
        public double rate_float;
    }

    public static class Bpi {
        @JsonProperty("USD")
        public USD uSD;
        @JsonProperty("GBP")
        public GBP gBP;
        @JsonProperty("EUR")
        public EUR eUR;
    }

    public static class Root {
        public Time time;
        public String disclaimer;
        public String chartName;
        public Bpi bpi;

        public Root() {
        }
    }
}