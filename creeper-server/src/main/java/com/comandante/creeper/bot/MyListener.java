package com.comandante.creeper.bot;

import com.comandante.creeper.bot.command.BitlyClient;
import com.comandante.creeper.bot.command.BitlyManager;
import com.comandante.creeper.bot.command.TwitterClient;
import com.comandante.creeper.bot.command.TwitterManager;
import com.comandante.creeper.bot.command.commands.BotCommand;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SentryManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.world.model.Room;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.comandante.creeper.server.player_communication.Color.RED;
import static com.comandante.creeper.server.player_communication.Color.RESET;

public class MyListener extends ListenerAdapter {

    private final GameManager gameManager;
    private final Integer bridgeRoomId;
    private final TwitterManager twitterManager;
    private final BitlyManager bitlyManager;

    public MyListener(GameManager gameManager, Integer bridgeRoomId, BitlyManager bitlyManager) {
        this.gameManager = gameManager;
        this.bridgeRoomId = bridgeRoomId;
        this.twitterManager = new TwitterManager(new TwitterClient(gameManager.getCreeperConfiguration()));
        this.bitlyManager = bitlyManager;
    }

    @Override
    public void onGenericMessage(GenericMessageEvent event) throws Exception {
        PlayerManager playerManager = gameManager.getPlayerManager();

        try {
            if (!(event instanceof MessageEvent)) {
                return;
            }
            if (event.getMessage().startsWith("!!")) {
                ArrayList<String> originalMessageParts = Lists.newArrayList(Arrays.asList(event.getMessage().split("!!")));
                originalMessageParts.remove(0);
                final String msg = Joiner.on(" ").join(originalMessageParts);
                BotCommand command = gameManager.getBotCommandFactory().getCommand((MessageEvent) event, msg);
                List<String> response = command.process();
                for (String line : response) {
                    gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(line);
                }
            }

            Optional<String> parseChatLineToTweetText = twitterManager.parseChatLineToTweetText(event.getMessage());
            parseChatLineToTweetText.ifPresent(s -> gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(s));
//            if (!parseChatLineToTweetText.isPresent()) {
//                Optional<BitlyClient.ShortenedUrlAndTitle> shortenedUrlAndTitle = bitlyManager.parseChatLineToTweetText(event.getMessage());
//                shortenedUrlAndTitle.ifPresent(s -> gameManager.getIrcBotService().getBot().getUserChannelDao().getChannel(gameManager.getCreeperConfiguration().getIrcChannel()).send().message(s.getShortenedUrl() + " | " + s.getTitle()));
//            }

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
}


