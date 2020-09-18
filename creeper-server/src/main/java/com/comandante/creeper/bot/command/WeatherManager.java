package com.comandante.creeper.bot.command;


import com.comandante.creeper.dropwizard.CreeperConfiguration;
import org.apache.log4j.Logger;

public class WeatherManager {

    private final CreeperConfiguration creeperConfiguration;
    private final AccuweatherManager accuweatherManager;

    private static final Logger log = Logger.getLogger(WeatherManager.class);


    public WeatherManager(CreeperConfiguration creeperConfiguration) {
        this.creeperConfiguration = creeperConfiguration;
        this.accuweatherManager = new AccuweatherManager(new AccuweatherClient(creeperConfiguration));
    }

    public String getWeather(String searchString) {
        String currentConditions = accuweatherManager.getCurrentConditions(searchString).toString().trim();
        currentConditions += " | " + accuweatherManager.getAQI(searchString);
        return currentConditions;
    }

    public String getFiveDayForecast(String searchString) {
        return accuweatherManager.getFiveDayForeCast(searchString);
    }

    public String getHourlyForecast(String searchString) {
        return accuweatherManager.getHourlyForecast(searchString);
    }

}
