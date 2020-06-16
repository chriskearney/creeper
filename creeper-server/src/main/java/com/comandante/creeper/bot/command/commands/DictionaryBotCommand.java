package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.Lists;
import com.google.common.collect.Sets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class DictionaryBotCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("dictionary", "define", "definition");
    static String helpUsage = "dictionary irony";
    static String helpDescription = "Obtain the definition for a word";

    public DictionaryBotCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        ArrayList<String> resp = Lists.newArrayList();
        String argumentString = joinArgs(args);
        try {
            resp.addAll(botCommandManager.getDictionaryManager().getDefinitionForWord(argumentString));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }
}
