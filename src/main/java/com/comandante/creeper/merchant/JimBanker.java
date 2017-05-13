package com.comandante.creeper.merchant;


import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.server.player_communication.Color;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.comandante.creeper.server.player_communication.Color.BOLD_ON;

public class JimBanker extends Merchant {
    private final static long phraseIntervalMs = 300000;
    private final static String NAME = "jim the banker";
    private final static String welcomeMessage = "Welcome to the First National Bank of Creeper.";
    private final static Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"bank", "banker", "jim the banker", "jim", "j", NAME}
    ));

    private final static String colorName = BOLD_ON + Color.CYAN + NAME + Color.RESET;

    public JimBanker(GameManager gameManager, List<MerchantItemForSale> merchantItemForSales) {
        super(gameManager, NAME, colorName, validTriggers, merchantItemForSales, welcomeMessage, MerchantType.BANK);
    }

    @Override
    public String getMenu() {
        return null;
    }
}

