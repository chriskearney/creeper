package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        MessageEvent messageEvent = getMessageEvent();
        Optional<String> lastArgString = Optional.empty();
        if (args.isEmpty() && messageEvent != null) {
            lastArgString = botCommandManager.getWeatherHistoryManager().getArgumentString(messageEvent.getUserHostmask().getNick());
        }
        resp.add(botCommandManager.getWeatherManager().getHourlyForecast(lastArgString.orElse(argumentString)));
        if ((!lastArgString.isPresent() && !Strings.isNullOrEmpty(argumentString)) && messageEvent != null) {
            botCommandManager.getWeatherHistoryManager().save(messageEvent.getUser().getNick(), argumentString);
        }
        return resp;
    }
}
