package com.comandante.creeper.command.commands;

import com.comandante.creeper.blackjack.BlackJack;
import com.comandante.creeper.blackjack.Deck;
import com.comandante.creeper.core_game.GameManager;
import com.google.common.collect.Lists;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;

import java.util.Arrays;
import java.util.List;

public class CardsCommand extends Command {

    final static List<String> validTriggers = Arrays.asList("cards");
    final static String description = "Display Random Cards";
    final static String correctUsage = "cards";

    public CardsCommand(GameManager gameManager) {
        super(gameManager, validTriggers, description, correctUsage);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        execCommand(ctx, e, () -> {
            Deck deck = new Deck();
            deck.shuffle();
            List<BlackJack.Card> cards = Lists.newArrayList(deck.next(), deck.next(), deck.next(), deck.next(), deck.deal());
            String asciiPlayingCardHand = BlackJack.getAsciiPlayingCardHand(cards);

            write(asciiPlayingCardHand + "\r\n");
        });
    }

}
