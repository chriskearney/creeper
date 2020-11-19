package com.comandante.creeper.bot.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class WeatherGovManagerTest {

    private final JsonParser jsonParser = new JsonParser();

    private final WeatherGovApi weatherGovApi = new WeatherGovApi() {
        @Override
        public JsonElement getAlertData(String latitude, String longitude) {
            try {
                String alertData = getResourceFileAsString("WEATHER_GOV_SAMPLE_ALERT_DATA.json");
                return jsonParser.parse(alertData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Test
    public void testAlertData() throws Exception {
        WeatherGovManager weatherGovManager = new WeatherGovManager(weatherGovApi);
        String alerts = weatherGovManager.getAlerts("sadf", "afd");
        System.out.println(alerts);
    }

    public String getResourceFileAsString(String fileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }
}