package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CheckNorrisBotCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("chuck");
    static String helpUsage = "chuck";
    static String helpDescription = "Random Chuck Norris Joke";

    public CheckNorrisBotCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        try {
            resp.addAll(botCommandManager.getChuckNorrisManager().getRandomChuckNorrisJoke());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }
}
