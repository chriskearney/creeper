package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.Creeper;
import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.bot.command.WeatherManager;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WeatherBotCommand extends BotCommand {


    private static final Logger log = Logger.getLogger(WeatherBotCommand.class);

    static Set<String> triggers = Sets.newHashSet("weather");
    static String helpUsage = "weather 97034";
    static String helpDescription = "Obtain weather using a zip code or city name.";

    private final WeatherManager weatherManager;

    public WeatherBotCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
        this.weatherManager = botCommandManager.getWeatherManager();
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        String argumentString = joinArgs(args);
        if (isNumeric(argumentString)) {
            try {
                resp.addAll(weatherManager.getWeather(argumentString));
            } catch (IOException e) {
                log.error("Problem trying to retrieve weather results by zipcode.", e);
            }
        } else {
            String[] split = argumentString.split(",");
            try {
                resp.addAll(weatherManager.getWeather(split[0], split[1]));
            } catch (IOException e) {
                log.error("Problem trying to retrieve weather results by city/state", e);
            }
        }
        if (resp.isEmpty()) {
            //
        }
        return resp;
    }
}
