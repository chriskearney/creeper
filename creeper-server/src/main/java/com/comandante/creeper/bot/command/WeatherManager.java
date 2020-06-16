package com.comandante.creeper.bot.command;


import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.ArrayMap;
import com.google.api.client.util.Lists;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WeatherManager {

    private final CreeperConfiguration creeperConfiguration;
    private final AccuweatherManager accuweatherManager;

    private static final Logger log = Logger.getLogger(WeatherManager.class);


    public WeatherManager(CreeperConfiguration creeperConfiguration) {
        this.creeperConfiguration = creeperConfiguration;
        this.accuweatherManager = new AccuweatherManager(new AccuweatherClient(creeperConfiguration));
    }

    public String getWeather(String searchString) {
        return accuweatherManager.getCurrentConditions(searchString).toString();
    }

    public String getFiveDayForecast(String searchString) {
        return accuweatherManager.getFiveDayForeCast(searchString);
    }

    public String getHourlyForecast(String searchString) {
        return accuweatherManager.getHourlyForecast(searchString);
    }

}
