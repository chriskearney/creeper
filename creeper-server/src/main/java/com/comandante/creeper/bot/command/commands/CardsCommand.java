package com.comandante.creeper.bot.command.commands;

import com.comandante.creeper.blackjack.BlackJack;
import com.comandante.creeper.blackjack.Deck;
import com.comandante.creeper.blackjack.Hand;
import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.common.CreeperUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CardsCommand extends BotCommand {

    static Set<String> triggers = Sets.newHashSet("cards");
    static String helpUsage = "cards";
    static String helpDescription = "Some random cards.";

    public CardsCommand(BotCommandManager botCommandManager) {
        super(botCommandManager, triggers, helpUsage, helpDescription);
    }

    @Override
    public List<String> process() {
        Deck deck = new Deck();
        deck.shuffle();
        List<BlackJack.Card> handOfCards = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            handOfCards.add(deck.next());
        }
        Set<Hand.Card> collect = handOfCards.stream()
                .map(convertCardFormats())
                .collect(Collectors.toSet());
        Hand.Category category = new Hand(collect).getCategory();
        Optional<String> handDescription = Optional.empty();
        if (category.equals(Hand.Category.FLUSH) || category.equals(Hand.Category.FOUR_OF_A_KIND) || category.equals(Hand.Category.FULL_HOUSE) || category.equals(Hand.Category.THREE_OF_A_KIND) || category.equals(Hand.Category.STRAIGHT) || category.equals(Hand.Category.STRAIGHT_FLUSH)) {
            handDescription = Optional.of(category.getName());
        }

        StringBuilder sb = new StringBuilder();
        if (getMessageEvent() != null) {
            String nickName = getMessageEvent().getUser().getNick();
            sb.append(nickName).append(": ");
        }
        handOfCards.forEach(card -> sb.append(card.type.textRepresentation).append(card.suit.textRepresentation).append(" / "));
        String responseMessage = CreeperUtils.replaceLast(sb.toString(), " / ", "");
        if (handDescription.isPresent()) {
            responseMessage += " - [" + handDescription.get() + "]";
        }
        return Lists.newArrayList(responseMessage);
    }

    public static Function<BlackJack.Card, Hand.Card> convertCardFormats() {
        return card -> {
            Hand.Suit suit = null;
            if (card.suit.equals(Deck.Suit.SPADES)) {
                suit = Hand.Suit.SPADES;
            } else if (card.suit.equals(Deck.Suit.CLUBS)) {
                suit = Hand.Suit.CLUBS;
            } else if (card.suit.equals(Deck.Suit.DIAMONDS)) {
                suit = Hand.Suit.DIAMONDS;
            } else if (card.suit.equals(Deck.Suit.HEARTS)) {
                suit = Hand.Suit.HEARTS;
            }
            if (suit == null) {
                throw new RuntimeException("Cards problem.");
            }

            Hand.Rank rank = null;
            if (card.type.equals(Deck.Type.ACE)) {
                rank = Hand.Rank.ACE;
            } else if (card.type.equals(Deck.Type.KING)) {
                rank = Hand.Rank.KING;
            } else if (card.type.equals(Deck.Type.QUEEN)) {
                rank = Hand.Rank.QUEEN;
            } else if (card.type.equals(Deck.Type.JACK)) {
                rank = Hand.Rank.JACK;
            } else if (card.type.equals(Deck.Type.TEN)) {
                rank = Hand.Rank.TEN;
            } else if (card.type.equals(Deck.Type.NINE)) {
                rank = Hand.Rank.NINE;
            } else if (card.type.equals(Deck.Type.EIGHT)) {
                rank = Hand.Rank.EIGHT;
            } else if (card.type.equals(Deck.Type.SEVEN)) {
                rank = Hand.Rank.SEVEN;
            } else if (card.type.equals(Deck.Type.SIX)) {
                rank = Hand.Rank.SIX;
            } else if (card.type.equals(Deck.Type.FIVE)) {
                rank = Hand.Rank.FIVE;
            } else if (card.type.equals(Deck.Type.FOUR)) {
                rank = Hand.Rank.FOUR;
            } else if (card.type.equals(Deck.Type.THREE)) {
                rank = Hand.Rank.THREE;
            } else if (card.type.equals(Deck.Type.TWO)) {
                rank = Hand.Rank.TWO;
            }

            if (rank == null) {
                throw new RuntimeException("Cards problem.");
            }
            return Hand.Card.fromRankSuit(rank, suit);
        };
    }
}
