package com.comandante.creeper.bot.command;

import com.comandante.creeper.bot.IrcBotService;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.RateLimiter;
import org.pircbotx.Colors;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class QuoteProcessor extends AbstractScheduledService {

    private final QuoteManager quoteManager;
    private final IrcBotService ircBotService;
    private final CreeperConfiguration creeperConfiguration;
    private final ArrayBlockingQueue<IrcQuoteRequest> quoteQueue = new ArrayBlockingQueue<>(1);
    private final RateLimiter rateLimiter = RateLimiter.create(.6);
    private User currentUser;

    private AtomicBoolean shouldStop = new AtomicBoolean(false);

    public QuoteProcessor(QuoteManager quoteManager, IrcBotService ircBotService, CreeperConfiguration creeperConfiguration) {
        this.quoteManager = quoteManager;
        this.ircBotService = ircBotService;
        this.creeperConfiguration = creeperConfiguration;
    }

    public boolean isEmpty() {
        return quoteQueue.isEmpty();
    }

    public void addIrcQuotes(List<QuoteManager.IrcQuote> ircQuotes, Optional<GenericMessageEvent> messageEvent) {
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
            e.printStackTrace();
        }
    }

    public boolean removeIfUserMatch(User user) {
        if (currentUser == null) {
            return false;
        }
        if (currentUser.equals(user)) {
            shouldStop.set(true);
            return true;
        }
        return false;
    }

    @Override
    protected void runOneIteration() throws Exception {
        try {
            IrcQuoteRequest poll = quoteQueue.poll();
            if (poll.getUser().isPresent()) {
                currentUser = poll.getUser().get();
            }
            if (poll == null) {
                return;
            }
            String matchesFound = "[" + poll.getIrcQuotes().size() + "] results found.";
            if (poll.getUser().isPresent() && poll.getIrcQuotes().size() > 100) {
                matchesFound += " Kicking " + poll.getUser().get().getNick() + " from the channel will immediately stop the bot from streaming these results.";
            }
            ircBotService.getBot().getUserChannelDao().getChannel(creeperConfiguration.getIrcChannel()).send().message(matchesFound);
            for (QuoteManager.IrcQuote quote : poll.getIrcQuotes()) {
                if (shouldStop.get()) {
                    continue;
                }
                rateLimiter.acquire();
                if (!shouldStop.get()) {
                    ircBotService.getBot().getUserChannelDao().getChannel(creeperConfiguration.getIrcChannel()).send().message(Colors.BOLD + quote.getKeyword() + "[" + quote.getNumber() + "]: " + Colors.BOLD + quote.getQuote());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        currentUser = null;
        shouldStop.set(false);
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
