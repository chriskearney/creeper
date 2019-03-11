package com.comandante.creeper.bot.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class AccuweatherManagerTest {

    private final JsonParser jsonParser = new JsonParser();

    private final AccuweatherAPI accuweatherAPI = new AccuweatherAPI() {
        @Override
        public JsonElement getOneDayForecast(String locationKey) {
            try {
                String oneDayForeCastJson = new String(Files.readAllBytes(Paths.get(getClass().getResource("ACCUWEATHER_ONE_DAY_FORECAST_BY_LOCATIONKEY.json").toURI())));
                return jsonParser.parse(oneDayForeCastJson);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JsonElement getLocationByPostalCode(String searchString) {
            try {
                String oneDayForeCastJson = new String(Files.readAllBytes(Paths.get(getClass().getResource("ACCUWEATHER_LOCATION_BY_POSTAL_CODE_EXAMPLE.json").toURI())));
                return jsonParser.parse(oneDayForeCastJson);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JsonElement getLocationByCity(String searchString) {
            try {
                String oneDayForeCastJson = getResourceFileAsString("ACCUWEATHER_LOCATION_BY_CITY_EXAMPLE.json");
                return jsonParser.parse(oneDayForeCastJson);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public JsonElement getCurrentConditions(String locationKey) {
            try {
                String oneDayForeCastJson = getResourceFileAsString("ACCUWEATHER_CURRENT_CONDITIONS_BY_LOCATIONKEY.json");
                return jsonParser.parse(oneDayForeCastJson);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };


    public String getResourceFileAsString(String fileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
        return null;
    }

    private AccuweatherManager accuweatherManager;

    @Before
    public void setUp() throws Exception {
        accuweatherManager = new AccuweatherManager(accuweatherAPI);
    }

    @Test
    public void testCurrentConditions() throws Exception {

        AccuweatherManager.AccuweatherReport currentConditions = accuweatherManager.getCurrentConditions("n/a");
        System.out.println(currentConditions);

    }

}