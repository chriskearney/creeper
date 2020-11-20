package com.comandante.creeper.bot.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.net.www.http.HttpClient;

import java.io.IOException;

public class WeatherGovApiClient implements WeatherGovApi {


    private final static String WEATHER_GOV_ALERTS_BY_POINT_API = "https://api.weather.gov/alerts/active?point=";
    private final static String WEATHER_GOV_ALERTS_BY_POINT_STATIONS_SUFFIX = "https://api.weather.gov/points/{lat,long}/stations";

    private final CloseableHttpClient httpClient;
    private final JsonParser jsonParser = new JsonParser();

    public WeatherGovApiClient() {
        this.httpClient = HttpClients.custom().setUserAgent("Creeper MUD Project.").build();
    }

    public JsonElement getAlertData(String latitude, String longitude) {
        HttpGet httpGet = new HttpGet(WEATHER_GOV_ALERTS_BY_POINT_API + latitude + "," + longitude);
        try {
            return getJsonElementFromRequest(httpGet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonElement getStations(String latitude, String longitude) {
        HttpGet httpGet = new HttpGet(WEATHER_GOV_ALERTS_BY_POINT_STATIONS_SUFFIX.replace("{lat,long}", latitude + "," + longitude));
        try {
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return jsonParser.parse(responseString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JsonElement getJsonElementFromRequest(HttpGet request) {
        try {
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return jsonParser.parse(responseString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
