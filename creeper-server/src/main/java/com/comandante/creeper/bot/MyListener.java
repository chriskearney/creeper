package com.comandante.creeper.bot;

import com.comandante.creeper.bot.command.*;
import com.comandante.creeper.bot.command.commands.BotCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SentryManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.world.model.Room;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.linkedin.urls.Url;
import com.linkedin.urls.detection.UrlDetector;
import com.linkedin.urls.detection.UrlDetectorOptions;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static com.comandante.creeper.server.player_communication.Color.RED;
import static com.comandante.creeper.server.player_communication.Color.RESET;

public class MyListener extends ListenerAdapter {

    private final GameManager gameManager;
    private final Integer bridgeRoomId;
    private final TwitterManager twitterManager;
    private final BitlyManager bitlyManager;
    private final YoutubeManager youtubeManager;

    public MyListener(GameManager gameManager, Integer bridgeRoomId, BitlyManager bitlyManager, YoutubeManager youtubeManager) {
        this.gameManager = gameManager;
        this.bridgeRoomId = bridgeRoomId;
        this.twitterManager = new TwitterManager(new TwitterClient(gameManager.getCreeperConfiguration()));
        this.bitlyManager = bitlyManager;
        this.youtubeManager = youtubeManager;
    }

    @Override
    public void onKick(KickEvent event) throws Exception {
        if (gameManager.getQuoteProcessor().removeIfUserMatch(event.getRecipient())) {
            gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(event.getRecipient().getNick() + " was kicked, stopping results.");
        }
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) throws Exception {
        BotCommand command = gameManager.getBotCommandFactory().getCommand((GenericMessageEvent) event, event.getMessage(), null);
        List<String> commandOutputResult = command.process();
        for (String line: commandOutputResult) {
            event.respondPrivateMessage(line);
        }
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        try {
            if (!(event instanceof MessageEvent)) {
                return;
            }
            if (event.getMessage().startsWith("!!")) {
                ArrayList<String> originalMessageParts = Lists.newArrayList(Arrays.asList(event.getMessage().split("!!")));
                originalMessageParts.remove(0);
                final String msg = Joiner.on(" ").join(originalMessageParts);
                BotCommand command = gameManager.getBotCommandFactory().getCommand((MessageEvent) event, msg, null);
                List<String> response = command.process();
                for (String line : response) {
                    gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(line);
                }
            }

            Optional<TwitterClient.TweetDetails> tweetDetails = twitterManager.parseChatLineToTweetText(event.getMessage());
            if (tweetDetails.isPresent() && !tweetDetails.get().getTweetText().isEmpty()) {
                List<String> twitterOutput = Lists.newArrayList(tweetDetails.get().getTweetText());
                String firstLine = twitterOutput.get(0);
                String modifiedFirstLine = "@" + tweetDetails.get().getScreeName() + ": " + firstLine;
                twitterOutput.remove(0);
                send(modifiedFirstLine);
                for (String line: twitterOutput) {
                    if (Strings.isNullOrEmpty(line) || line.trim().isEmpty()) {
                        continue;
                    }
                    send(line);
                }
                DateFormat dateFormat = new SimpleDateFormat("hh:mm aa - MMM dd, yyyy");
                String formattedCreatedAt = dateFormat.format(tweetDetails.get().getCreatedAt());
                send("[ " + formattedCreatedAt + " | retweets: " + putCommas(tweetDetails.get().getReTweets()) + " | likes: " + putCommas(tweetDetails.get().getLikes()) + " ]");
            }

            Optional<String> videoIdFromYoutubeUrl = youtubeManager.getVideoIdFromChatLine(event.getMessage());
            if (videoIdFromYoutubeUrl.isPresent()) {
                Optional<String> videoInfo = youtubeManager.getVideoInfo(videoIdFromYoutubeUrl.get());
                Optional<BitlyClient.ShortenedUrl> onlyBitlyUrl = bitlyManager.getOnlyBitlyUrl(event.getMessage());
                String youTubeResponse = null;
                if (videoInfo.isPresent()) {
                    youTubeResponse = videoInfo.get();
                    if (onlyBitlyUrl.isPresent()) {
                        youTubeResponse = youTubeResponse + " | " + onlyBitlyUrl.get().getLink();
                    }
                }
                if (!Strings.isNullOrEmpty(youTubeResponse)) {
                    send(youTubeResponse);
                }
            }

            if (!tweetDetails.isPresent() && !videoIdFromYoutubeUrl.isPresent()) {
                Optional<BitlyClient.ShortenedUrlAndTitle> shortenedUrlAndTitle = bitlyManager.parseChatLineToTweetText(event.getMessage());
                shortenedUrlAndTitle.ifPresent(s -> {
                    if (!Strings.isNullOrEmpty(s.getTitle())) {
                        gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(s.getShortenedUrl() + " | " + s.getTitle());
                    } else {
                        gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(s.getShortenedUrl());
                    }
                });
            }

            Room bridgeRoom = gameManager.getRoomManager().getRoom(bridgeRoomId);
            Set<Player> presentPlayers = bridgeRoom.getPresentPlayers();
            for (Player presentPlayer : presentPlayers) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(RED);
                stringBuilder.append("<").append(event.getUser().getNick()).append("> ").append(event.getMessage());
                stringBuilder.append(RESET);
                gameManager.getChannelUtils().write(presentPlayer.getPlayerId(), stringBuilder.append("\r\n").toString(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SentryManager.logSentry(this.getClass(), e, "IRC Listener Exception!");
        }
    }

    private void send(String msg) {
        gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(msg);
    }

    private String putCommas(int source) {
        return NumberFormat.getNumberInstance(Locale.US).format(source);
    }

    public static Optional<String> extractFirstUrl(String chatLine) {
        UrlDetector parser = new UrlDetector(chatLine, UrlDetectorOptions.Default);
        List<Url> found = parser.detect();

        for(Url url : found) {
            if (url.getOriginalUrl().startsWith("https://") || url.getOriginalUrl().startsWith("http://")) {
                return Optional.ofNullable(url.getFullUrl());
            }
        }
        return Optional.empty();
    }


//    @Subscribe
//    public void receiveWeatherAlertEvent(WeatherAlertReceivedEvent weatherAlertReceivedEvent) {
//        System.out.println("hi");
//        send(weatherAlertReceivedEvent.getWeatherAlert());
//    }
}


