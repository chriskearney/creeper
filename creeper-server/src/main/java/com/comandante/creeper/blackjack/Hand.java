package com.comandante.creeper.blackjack;

import com.google.common.collect.EnumMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Ordering;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.comandante.creeper.blackjack.Hand.Category.FLUSH;
import static com.comandante.creeper.blackjack.Hand.Category.FOUR_OF_A_KIND;
import static com.comandante.creeper.blackjack.Hand.Category.FULL_HOUSE;
import static com.comandante.creeper.blackjack.Hand.Category.HIGH_CARD;
import static com.comandante.creeper.blackjack.Hand.Category.ONE_PAIR;
import static com.comandante.creeper.blackjack.Hand.Category.STRAIGHT;
import static com.comandante.creeper.blackjack.Hand.Category.STRAIGHT_FLUSH;
import static com.comandante.creeper.blackjack.Hand.Category.THREE_OF_A_KIND;
import static com.comandante.creeper.blackjack.Hand.Category.TWO_PAIR;
import static com.comandante.creeper.blackjack.Hand.Rank.ACE;
import static com.comandante.creeper.blackjack.Hand.Rank.EIGHT;
import static com.comandante.creeper.blackjack.Hand.Rank.FIVE;
import static com.comandante.creeper.blackjack.Hand.Rank.FOUR;
import static com.comandante.creeper.blackjack.Hand.Rank.JACK;
import static com.comandante.creeper.blackjack.Hand.Rank.KING;
import static com.comandante.creeper.blackjack.Hand.Rank.NINE;
import static com.comandante.creeper.blackjack.Hand.Rank.QUEEN;
import static com.comandante.creeper.blackjack.Hand.Rank.SEVEN;
import static com.comandante.creeper.blackjack.Hand.Rank.SIX;
import static com.comandante.creeper.blackjack.Hand.Rank.TEN;
import static com.comandante.creeper.blackjack.Hand.Rank.THREE;
import static com.comandante.creeper.blackjack.Hand.Rank.TWO;
import static com.comandante.creeper.blackjack.Hand.Suit.CLUBS;
import static com.comandante.creeper.blackjack.Hand.Suit.DIAMONDS;
import static com.comandante.creeper.blackjack.Hand.Suit.HEARTS;
import static com.comandante.creeper.blackjack.Hand.Suit.SPADES;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Ordering.from;
import static com.google.common.collect.Ordering.natural;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
public class Hand implements Comparable<Hand> {
    public final Category category;

    private final LinkedList<Rank> distinctRanks = new LinkedList<>();

    private final Set<Card> cards;

    public Hand(Set<Card> cards) {
        this.cards = cards;
        checkArgument(cards.size() == 5);
        Set<Suit> suits = EnumSet.noneOf(Suit.class);
        Multiset<Rank> ranks = EnumMultiset.create(Rank.class);
        for (Card card : cards) {
            suits.add(card.suit);
            ranks.add(card.rank);
        }
        Set<Entry<Rank>> entries = ranks.entrySet();
        for (Entry<Rank> entry : byCountThenRank.immutableSortedCopy(entries)) {
            distinctRanks.addFirst(entry.getElement());
        }
        Rank first = distinctRanks.getFirst();
        int distinctCount = distinctRanks.size();
        if (distinctCount == 5) {
            boolean flush = suits.size() == 1;
            if (first.ordinal() - distinctRanks.getLast().ordinal() == 4) {
                category = flush ? STRAIGHT_FLUSH : STRAIGHT;
            }
            else if (first == ACE && distinctRanks.get(1) == FIVE) {
                category = flush ? STRAIGHT_FLUSH : STRAIGHT;
                // ace plays low, move to end
                distinctRanks.addLast(distinctRanks.removeFirst());
            }
            else {
                category = flush ? FLUSH : HIGH_CARD;
            }
        }
        else if (distinctCount == 4) {
            category = ONE_PAIR;
        }
        else if (distinctCount == 3) {
            category = ranks.count(first) == 2 ? TWO_PAIR : THREE_OF_A_KIND;
        }
        else {
            category = ranks.count(first) == 3 ? FULL_HOUSE : FOUR_OF_A_KIND;
        }
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public final int compareTo(Hand that) {
        return byCategoryThenRanks.compare(this, that);
    }

    private static final Ordering<Entry<Rank>> byCountThenRank;

    private static final Comparator<Hand> byCategoryThenRanks;



    static {
        Comparator<Entry<Rank>> byCount = comparingInt(Entry::getCount);
        Comparator<Entry<Rank>> byRank = comparing(Entry::getElement);
        byCountThenRank = from(byCount.thenComparing(byRank));
        Comparator<Hand> byCategory = comparing((Hand hand) -> hand.category);
        Function<Hand, Iterable<Rank>> getRanks =
                (Hand hand) -> hand.distinctRanks;
        Comparator<Hand> byRanks =
                comparing(getRanks, natural().lexicographical());
        byCategoryThenRanks = byCategory.thenComparing(byRanks);
    }

    public enum Category {
        HIGH_CARD("High Card"),
        ONE_PAIR("One Pair"),
        TWO_PAIR("Two Pair"),
        THREE_OF_A_KIND("Three of a Kind"),
        STRAIGHT("Straight"),
        FLUSH("Flush"),
        FULL_HOUSE("Full House"),
        FOUR_OF_A_KIND("Four of a Kind"),
        STRAIGHT_FLUSH("Straight Flush");

        private final String name;

        Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Rank {
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN,
        JACK,
        QUEEN,
        KING,
        ACE;
    }

    public enum Suit {
        DIAMONDS,
        CLUBS,
        HEARTS,
        SPADES;
    }

    public enum Card {
        TWO_DIAMONDS(TWO, DIAMONDS),
        THREE_DIAMONDS(THREE, DIAMONDS),
        FOUR_DIAMONDS(FOUR, DIAMONDS),
        FIVE_DIAMONDS(FIVE, DIAMONDS),
        SIX_DIAMONDS(SIX, DIAMONDS),
        SEVEN_DIAMONDS(SEVEN, DIAMONDS),
        EIGHT_DIAMONDS(EIGHT, DIAMONDS),
        NINE_DIAMONDS(NINE, DIAMONDS),
        TEN_DIAMONDS(TEN, DIAMONDS),
        JACK_DIAMONDS(JACK, DIAMONDS),
        QUEEN_DIAMONDS(QUEEN, DIAMONDS),
        KING_DIAMONDS(KING, DIAMONDS),
        ACE_DIAMONDS(ACE, DIAMONDS),

        TWO_CLUBS(TWO, CLUBS),
        THREE_CLUBS(THREE, CLUBS),
        FOUR_CLUBS(FOUR, CLUBS),
        FIVE_CLUBS(FIVE, CLUBS),
        SIX_CLUBS(SIX, CLUBS),
        SEVEN_CLUBS(SEVEN, CLUBS),
        EIGHT_CLUBS(EIGHT, CLUBS),
        NINE_CLUBS(NINE, CLUBS),
        TEN_CLUBS(TEN, CLUBS),
        JACK_CLUBS(JACK, CLUBS),
        QUEEN_CLUBS(QUEEN, CLUBS),
        KING_CLUBS(KING, CLUBS),
        ACE_CLUBS(ACE, CLUBS),

        TWO_HEARTS(TWO, HEARTS),
        THREE_HEARTS(THREE, HEARTS),
        FOUR_HEARTS(FOUR, HEARTS),
        FIVE_HEARTS(FIVE, HEARTS),
        SIX_HEARTS(SIX, HEARTS),
        SEVEN_HEARTS(SEVEN, HEARTS),
        EIGHT_HEARTS(EIGHT, HEARTS),
        NINE_HEARTS(NINE, HEARTS),
        TEN_HEARTS(TEN, HEARTS),
        JACK_HEARTS(JACK, HEARTS),
        QUEEN_HEARTS(QUEEN, HEARTS),
        KING_HEARTS(KING, HEARTS),
        ACE_HEARTS(ACE, HEARTS),

        TWO_SPADES(TWO, SPADES),
        THREE_SPADES(THREE, SPADES),
        FOUR_SPADES(FOUR, SPADES),
        FIVE_SPADES(FIVE, SPADES),
        SIX_SPADES(SIX, SPADES),
        SEVEN_SPADES(SEVEN, SPADES),
        EIGHT_SPADES(EIGHT, SPADES),
        NINE_SPADES(NINE, SPADES),
        TEN_SPADES(TEN, SPADES),
        JACK_SPADES(JACK, SPADES),
        QUEEN_SPADES(QUEEN, SPADES),
        KING_SPADES(KING, SPADES),
        ACE_SPADES(ACE, SPADES);

        public final Rank rank;

        public final Suit suit;

        Card(Rank rank, Suit suit) {
            this.rank = rank;
            this.suit = suit;
        }

        public static Card fromRankSuit(Rank rank, Suit suit) {
            Optional<Card> first = Arrays.stream(Card.values()).filter(card -> card.rank.equals(rank) && card.suit.equals(suit)).findFirst();
            if (first.isPresent()) {
                return first.get();
            } else {
                throw new RuntimeException("Cards problem");
            }
        }

        public Rank getRank() {
            return rank;
        }

        public Suit getSuit() {
            return suit;
        }
    }

    public boolean isRoyalFlush() {
        Optional<Card> aceCard = cards.stream().filter(card -> card.getRank().equals(Rank.ACE)).findAny();
        Suit desiredSuit;
        if (aceCard.isPresent()) {
            desiredSuit = aceCard.get().getSuit();
        } else {
            return false;
        }

        Optional<Card> kingSuitedCard = cards.stream().filter(card -> card.getRank().equals(KING)).filter(card -> card.getSuit().equals(desiredSuit)).findAny();

        if (!kingSuitedCard.isPresent()) {
           return false;
        }

        Optional<Card> queenSuitedCard = cards.stream().filter(card -> card.getRank().equals(QUEEN)).filter(card -> card.getSuit().equals(desiredSuit)).findAny();

        if (!queenSuitedCard.isPresent()) {
            return false;
        }

        Optional<Card> jackSuitedCard = cards.stream().filter(card -> card.getRank().equals(JACK)).filter(card -> card.getSuit().equals(desiredSuit)).findAny();

        if (!jackSuitedCard.isPresent()) {
            return false;
        }

        Optional<Card> tenSuitedCard = cards.stream().filter(card -> card.getRank().equals(TEN)).filter(card -> card.getSuit().equals(desiredSuit)).findAny();

        if (!tenSuitedCard.isPresent()) {
            return false;
        }

        return true;
    }
}