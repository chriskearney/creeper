package com.comandante.creeper.bot.command;


import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;

public class WeatherManager {

    private final CreeperConfiguration creeperConfiguration;
    private final AccuweatherManager accuweatherManager;

    private static final Logger log = Logger.getLogger(WeatherManager.class);


    public WeatherManager(CreeperConfiguration creeperConfiguration, EventBus eventBus) {
        this.creeperConfiguration = creeperConfiguration;
        this.accuweatherManager = new AccuweatherManager(new AccuweatherClient(creeperConfiguration), eventBus, new WeatherGovManager( new WeatherGovApiClient()));
    }

    public String getWeather(String searchString) {
        String currentConditions = accuweatherManager.getCurrentConditions(searchString).toString();
        String trim = currentConditions.trim();
        return trim + " | " + accuweatherManager.getAQI(searchString);
    }

    public String getFiveDayForecast(String searchString) {
        return accuweatherManager.getFiveDayForeCast(searchString);
    }

    public String getHourlyForecast(String searchString) {
        return accuweatherManager.getHourlyForecast(searchString);
    }

}
