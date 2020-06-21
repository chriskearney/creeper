package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WeatherBotCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("weather");
    static String helpUsage = "weather 97034";
    static String helpDescription = "Obtain weather using a zip code or city name.";

    public WeatherBotCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        String argumentString = joinArgs(args);
        String weather = botCommandManager.getWeatherManager().getWeather(argumentString);
        resp.add(weather);
        return resp;
    }
}
