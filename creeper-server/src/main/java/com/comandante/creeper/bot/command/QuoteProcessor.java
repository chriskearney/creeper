package com.comandante.creeper.bot.command;

import com.comandante.creeper.bot.IrcBotService;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.RateLimiter;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class QuoteProcessor extends AbstractScheduledService {

    private final QuoteManager quoteManager;
    private final IrcBotService ircBotService;
    private final CreeperConfiguration creeperConfiguration;
    private final ArrayBlockingQueue<IrcQuoteRequest> quoteQueue = new ArrayBlockingQueue<>(3);
    private final RateLimiter rateLimiter = RateLimiter.create(.6);

    public QuoteProcessor(QuoteManager quoteManager, IrcBotService ircBotService, CreeperConfiguration creeperConfiguration) {
        this.quoteManager = quoteManager;
        this.ircBotService = ircBotService;
        this.creeperConfiguration = creeperConfiguration;
    }

    public void addIrcQuotes(List<QuoteManager.IrcQuote> ircQuotes, Optional<MessageEvent> messageEvent) {
        Optional<User> user = Optional.empty();
        if (messageEvent.isPresent()) {
            user = Optional.ofNullable(messageEvent.get().getUser());
            Optional<User> finalUser = user;
            if (quoteQueue.stream().anyMatch(ircQuoteRequest -> ircQuoteRequest.getUser().equals(finalUser.get()))) {
                return;
            }
        }
        try {
            quoteQueue.add(new IrcQuoteRequest(ircQuotes, user));
        } catch (IllegalStateException e) {

        }
    }

    @Override
    protected void runOneIteration() throws Exception {
        IrcQuoteRequest poll = quoteQueue.poll();
        if (poll == null) {
            return;
        }
        for (QuoteManager.IrcQuote quote: poll.getIrcQuotes()) {
            rateLimiter.acquire();
            ircBotService.getBot().getUserChannelDao().getChannel(creeperConfiguration.getIrcChannel()).send().message(Colors.BOLD + quote.getKeyword() + "[" + quote.getNumber() + "]: " + Colors.BOLD + quote.getQuote());
        }
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 1, TimeUnit.SECONDS);
    }

    public static class IrcQuoteRequest {
        private final List<QuoteManager.IrcQuote> ircQuotes;
        private final Optional<User> user;

        public IrcQuoteRequest(List<QuoteManager.IrcQuote> ircQuotes, Optional<User> user) {
            this.ircQuotes = ircQuotes;
            this.user = user;
        }

        public List<QuoteManager.IrcQuote> getIrcQuotes() {
            return ircQuotes;
        }

        public Optional<User> getUser() {
            return user;
        }
    }
}
