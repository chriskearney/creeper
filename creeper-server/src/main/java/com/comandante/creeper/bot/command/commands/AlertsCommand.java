package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.GenericData;
import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AlertsCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("alerts");
    static String helpUsage = "alerts 97034";
    static String helpDescription = "Obtain weather alerts using a zip code or city name, using the National Weather Service.";

    public AlertsCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        String argumentString = joinArgs(args);
        GenericMessageEvent messageEvent = getMessageEvent();
        Optional<String> lastArgString = Optional.empty();
        if (args.isEmpty() && messageEvent != null) {
            lastArgString = botCommandManager.getWeatherHistoryManager().getArgumentString(messageEvent.getUserHostmask().getNick());
        }
        Optional<List<String>> alerts = botCommandManager.getWeatherManager().getAlerts(lastArgString.orElse(argumentString));
        if ((!lastArgString.isPresent() && !Strings.isNullOrEmpty(argumentString)) && messageEvent != null) {
            botCommandManager.getWeatherHistoryManager().save(messageEvent.getUser().getNick(), argumentString);
        }
        if (alerts.isPresent()) {
            resp.addAll(alerts.get());
        }
        return resp;
    }
}
