package com.comandante.creeper.bot.command;

import org.testng.collections.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class QuoteManager {

    private final Map<String, List<IrcQuote>> ircQuoteMap;

    public QuoteManager(Map<String, List<IrcQuote>> ircQuoteMap) {
        this.ircQuoteMap = ircQuoteMap;
    }

    public void save(IrcQuote ircQuote) {
        List<IrcQuote> ircQuotes = ircQuoteMap.getOrDefault(ircQuote.getKeyword(), Lists.newArrayList());
        ircQuotes.add(ircQuote);
        ircQuoteMap.put(ircQuote.getKeyword(), ircQuotes);
    }

    public List<IrcQuote> getByKeyword(String keyword) {
        return ircQuoteMap.get(keyword);
    }

    public List<IrcQuote> grep(String keyword) {
        List<IrcQuote> matches = Lists.newArrayList();
        Set<Map.Entry<String, List<IrcQuote>>> entries = ircQuoteMap.entrySet();
        Iterator<Map.Entry<String, List<IrcQuote>>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<IrcQuote>> next = iterator.next();
            List<IrcQuote> found = next.getValue().stream().filter(ircQuote -> Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(ircQuote.getQuote()).find()).collect(Collectors.toList());
            matches.addAll(found);
        }
        return matches;
    }

    public List<IrcQuote> getByAuthor(String author) {
        List<IrcQuote> quotesByAuthor = Lists.newArrayList();
        for (Map.Entry<String, List<IrcQuote>> next : ircQuoteMap.entrySet()) {
            for (IrcQuote ircQuote : next.getValue()) {
                if (ircQuote.getAuthor().equals(author)) {
                    quotesByAuthor.add(ircQuote);
                }
            }
        }
        return quotesByAuthor;
    }

    public static class IrcQuote {
        private final String author;
        private final String keyword;
        private final String quote;

        public IrcQuote(String author, String keyword, String quote) {
            this.author = author;
            this.keyword = keyword;
            this.quote = quote;
        }

        public String getAuthor() {
            return author;
        }

        public String getKeyword() {
            return keyword;
        }

        public String getQuote() {
            return quote;
        }
    }
}
