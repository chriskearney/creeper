package com.comandante.creeper.merchant;

import com.comandante.creeper.Items.Loot;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcStats;
import com.comandante.creeper.server.Color;
import com.comandante.creeper.world.Area;
import com.google.common.base.Optional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.comandante.creeper.server.Color.BOLD_ON;
import static com.comandante.creeper.server.Color.RESET;

public class LloydBartender extends Merchant {
    private final static long phraseIntervalMs = 300000;
    private final static String NAME = "lloyd the bartender";
    private final static String welcomeMessage = " _        _        _______           ______   _  _______ \r\n" +
            "( \\      ( \\      (  ___  )|\\     /|(  __  \\ ( )(  ____ \\\r\n" +
            "| (      | (      | (   ) |( \\   / )| (  \\  )|/ | (    \\/\r\n" +
            "| |      | |      | |   | | \\ (_) / | |   ) |   | (_____ \r\n" +
            "| |      | |      | |   | |  \\   /  | |   | |   (_____  )\r\n" +
            "| |      | |      | |   | |   ) (   | |   ) |         ) |\r\n" +
            "| (____/\\| (____/\\| (___) |   | |   | (__/  )   /\\____) |\r\n" +
            "(_______/(_______/(_______)   \\_/   (______/    \\_______)\r\n" +
            "                                                         ";
    private final static Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"lloyd", "bartender", "barkeep", "Lloyd", "LLOYD", NAME}
    ));

    private final static String colorName = BOLD_ON + Color.CYAN + NAME  + Color.RESET ;

    public LloydBartender(GameManager gameManager, Loot loot, Map<Integer, MerchantItemForSale> merchantItemForSales) {
        super(gameManager, NAME, colorName, validTriggers, merchantItemForSales, welcomeMessage);
    }
}
