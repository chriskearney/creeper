package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.bot.command.QuoteManager;
import com.google.api.client.util.Lists;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LearnCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("learn", "l");
    static String helpUsage = "learn <keyword> <description>";
    static String helpDescription = "Store a quote for a keyword.";

    public LearnCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        List<String> strings = args;
        String keyword = strings.remove(0);
        String quote = joinArgs(strings);
        QuoteManager quoteManager = botCommandManager.getQuoteManager();
        String nick = null;
        if (getMessageEvent() != null) {
            nick = getMessageEvent().getUser().getNick();
        } else {
            nick = getPlayer().getPlayerName();
        }
        quoteManager.save(new QuoteManager.IrcQuote(nick, keyword, quote));
        return Collections.singletonList("Saved quote to " + keyword + "[" + quoteManager.getByKeyword(keyword).size() + "]");
    }
}
