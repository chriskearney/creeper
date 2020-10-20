package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.*;

public class BtcCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("btc");
    static String helpUsage = "btc";
    static String helpDescription = "Get current bitcoin price.";

    public BtcCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        return Collections.singletonList(botCommandManager.getCoindeskManager().getBitCoinPriceinDollars());
    }
}
