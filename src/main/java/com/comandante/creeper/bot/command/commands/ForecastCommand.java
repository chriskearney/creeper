package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ForecastCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("forecast");
    static String helpUsage = "forecast 97034";
    static String helpDescription = "Obtain weather forecast using a zip code or city name.";

    public ForecastCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        String argumentString = joinArgs(args);
        if (isNumeric(argumentString)) {
//            try {
//               // resp.addAll(botCommandManager.getWeatherManager().getWeatherForecast(argumentString));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else {
            String[] split = argumentString.split(",");
//            try {
//               // resp.addAll(botCommandManager.getWeatherManager().getWeatherForecast(split[0], split[1]));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return resp;
    }
}
