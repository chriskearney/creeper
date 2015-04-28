package com.comandante.creeper.merchant;

import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.Loot;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.stat.Stats;
import com.comandante.creeper.world.Area;
import com.google.common.base.Optional;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class Merchant extends CreeperEntity {

    private long lastPhraseTimestamp;
    private final GameManager gameManager;
    private final String name;
    private final String colorName;
    private final Set<String> validTriggers;
    private final Map<Integer, MerchantItemForSale> merchantItemForSales;
    private final String welcomeMessage;

    public Merchant(GameManager gameManager, String name, String colorName, Set<String> validTriggers, Map<Integer, MerchantItemForSale> merchantItemForSales, String welcomeMessage) {
        this.gameManager = gameManager;
        this.name = name;
        this.colorName = colorName;
        this.validTriggers = validTriggers;
        this.merchantItemForSales = merchantItemForSales;
        this.welcomeMessage = welcomeMessage;
    }

    public String getMenu() {
        Table t = new Table(3, BorderStyle.CLASSIC_COMPATIBLE,
                ShownBorders.HEADER_FOOTER_FIRST_AND_LAST_COLLUMN);
        t.setColumnWidth(0, 2, 5);
        t.setColumnWidth(1, 5, 5);
        t.setColumnWidth(2, 50, 69);
        t.addCell("#");
        t.addCell("price");
        t.addCell("description");
        int i = 1;
        Iterator<Map.Entry<Integer, MerchantItemForSale>> entries = merchantItemForSales.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, MerchantItemForSale> next = entries.next();
            t.addCell(String.valueOf(next.getKey()));
            t.addCell(String.valueOf(next.getValue().getCost()));
            t.addCell(next.getValue().getItem().getItemDescription());
            i++;
        }
        return t.render();
    }

    @Override
    public void run() {

    }

    public long getLastPhraseTimestamp() {
        return lastPhraseTimestamp;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public String getName() {
        return name;
    }

    public String getColorName() {
        return colorName;
    }

    public Set<String> getValidTriggers() {
        return validTriggers;
    }

    public Map<Integer, MerchantItemForSale> getMerchantItemForSales() {
        return merchantItemForSales;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}
