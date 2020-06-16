package com.comandante.creeper;

import com.comandante.creeper.blackjack.BlackJack;
import com.comandante.creeper.blackjack.Deck;
import com.comandante.creeper.blackjack.Hand;
import com.comandante.creeper.common.CreeperUtils;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerClass;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.player.PlayerRole;
import com.comandante.creeper.stats.DefaultStats;
import com.comandante.creeper.stats.modifier.StatsModifierFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.comandante.creeper.bot.command.commands.CardsCommand.convertCardFormats;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreeperUtilsTest {

    @Test
    @Ignore
    public void testCombineStrings() throws Exception {
        String[] strings = new String[2];
        strings[0] = "feet";
        strings[1] = "hand";
        PlayerMetadata playerMetadata = new PlayerMetadata(
                "usertest",
                "Testtest",
                Creeper.createPlayerId("usertest"),
                DefaultStats.DEFAULT_PLAYER.createStats(),
                0,
                Sets.newHashSet(PlayerRole.MORTAL),
                strings,
                0,
                new String[0],
                Maps.newHashMap(),
                PlayerClass.BASIC,
                Maps.newConcurrentMap(),
                null, null, null);
        GameManager gameManager = mock(GameManager.class);
        StatsModifierFactory statsModifierFactory = mock(StatsModifierFactory.class);
        when(statsModifierFactory.getStatsModifier(Matchers.any())).thenReturn(DefaultStats.DEFAULT_PLAYER.createStats());
        when(gameManager.getStatsModifierFactory()).thenReturn(statsModifierFactory);
        PlayerManager playerManager = mock(PlayerManager.class);
        when(playerManager.getPlayerMetadata(Matchers.any())).thenReturn(java.util.Optional.ofNullable(playerMetadata));
        when(gameManager.getPlayerManager()).thenReturn(playerManager);
        EntityManager entityManager = mock(EntityManager.class);
//        when(entityManager.getItemEntity(Matchers.startsWith("feet"))).thenReturn(java.util.Optional.ofNullable(ItemType.BERSEKER_BOOTS.create()));
//        when(entityManager.getItemEntity(Matchers.startsWith("hand"))).thenReturn(java.util.Optional.ofNullable(ItemType.BERSERKER_BATON.create()));
        when(gameManager.getEntityManager()).thenReturn(entityManager);
        Player usertest = new Player("usertest", gameManager);

        String s = CreeperUtils.printStringsNextToEachOther(Lists.newArrayList(usertest.getLookString(), usertest.getLookString()), " | ");

        System.out.println(s);


        // System.out.println(usertest.getLookString().replaceAll("\u001B\\[[;\\d]*m", " | "));


    }

    @Test
    @Ignore
    public void simulatePokerHands() throws Exception {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            Deck deck = new Deck();
            deck.shuffle();
            List<BlackJack.Card> handOfCards = Lists.newArrayList();
            for (int o = 0; o < 5; o++) {
                handOfCards.add(deck.next());
            }
            Set<Hand.Card> collect = handOfCards.stream()
                    .map(convertCardFormats())
                    .collect(Collectors.toSet());
            Hand hand = new Hand(collect);
            if (i % 10000 == 0) {
                System.out.println("Attempt: " + i);
            }
            HashSet<Hand.Card> compareHand = Sets.newHashSet();
            compareHand.add(Hand.Card.FOUR_SPADES);
            compareHand.add(Hand.Card.FIVE_SPADES);
            compareHand.add(Hand.Card.SIX_SPADES);
            compareHand.add(Hand.Card.TWO_SPADES);
            compareHand.add(Hand.Card.THREE_SPADES);
            Hand benchmark = new Hand(compareHand);
            int i1 = benchmark.compareTo(hand);
            if (benchmark.compareTo(hand) == 0) {
                System.out.println("It took " + i + " attempts to beat svens hand.");
                break;
            }
        }
    }
}