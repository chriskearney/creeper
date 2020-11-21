package com.comandante.creeper.bot.command;


import com.comandante.creeper.bot.IrcBotService;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.google.common.eventbus.EventBus;
import org.apache.log4j.Logger;

public class WeatherManager {

    private final CreeperConfiguration creeperConfiguration;
    private final AccuweatherManager accuweatherManager;
    private final WeatherGovManager weatherGovManager;

    private static final Logger log = Logger.getLogger(WeatherManager.class);


    public WeatherManager(CreeperConfiguration creeperConfiguration, IrcBotService ircBotService) {
        this.creeperConfiguration = creeperConfiguration;
        this.weatherGovManager = new WeatherGovManager(new WeatherGovApiClient());
        this.accuweatherManager = new AccuweatherManager(new AccuweatherClient(creeperConfiguration), ircBotService, weatherGovManager, creeperConfiguration);
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

    public String getNwsCurrentConditions(String searchString) {
        String latLong = accuweatherManager.getCords(searchString);
        return weatherGovManager.getCurrentWeather(latLong.split(",")[0], latLong.split(",")[1]);
    }
}
