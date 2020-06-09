package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HourlyCommand extends BotCommand {
    static Set<String> triggers = Sets.newHashSet("hourly");
    static String helpUsage = "hourly 97034";
    static String helpDescription = "Obtain an hourly weather forecast using a zip code or city name.";

    public HourlyCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        String argumentString = joinArgs(args);
        resp.add(botCommandManager.getWeatherManager().getHourlyForecast(argumentString));
        return resp;
    }
}
