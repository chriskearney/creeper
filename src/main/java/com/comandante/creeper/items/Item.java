package com.comandante.creeper.items;


import com.comandante.creeper.core_game.service.TimeTracker;
import com.comandante.creeper.stats.Stats;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Item implements Serializable {

    private final String itemName;
    private final String itemDescription;
    private final String internalItemName;
    private final List<String> itemTriggers;
    private final String restingName;
    private final String itemId;
    private int numberOfUses;
    private boolean isWithPlayer;
    private final Loot loot;
    private final int itemHalfLifeTicks;
    private Equipment equipment;
    private final Rarity rarity;
    private final int valueInGold;
    private Set<Effect> effects;
    private boolean hasBeenWithPlayer;
    private final int maxUses;
    private final boolean isDisposable;
    private Set<TimeTracker.TimeOfDay> validTimeOfDays;
    private final Stats itemApplyStats;
    private final Map<Double, Effect> attackEffects;

    public static final String CORPSE_INTENAL_NAME = "corpse";

    protected Item(String itemName, String itemDescription, String internalItemName, List<String> itemTriggers, String restingName, String itemId, int numberOfUses, boolean isWithPlayer, Loot loot, int itemHalfLifeTicks, Equipment equipment, Rarity rarity, int valueInGold, Set<Effect> effects, boolean hasBeenWithPlayer, int maxUses, boolean isDisposable, Set<TimeTracker.TimeOfDay> validTimeOfDays, Stats itemApplyStats, Map<Double, Effect> attackEffects) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.internalItemName = internalItemName;
        this.itemTriggers = itemTriggers;
        this.restingName = restingName;
        this.itemId = itemId;
        this.numberOfUses = numberOfUses;
        this.isWithPlayer = isWithPlayer;
        this.loot = loot;
        this.itemHalfLifeTicks = itemHalfLifeTicks;
        this.equipment = equipment;
        this.rarity = rarity;
        this.valueInGold = valueInGold;
        this.effects = effects;
        this.hasBeenWithPlayer = hasBeenWithPlayer;
        this.maxUses = maxUses;
        this.isDisposable = isDisposable;
        this.validTimeOfDays = validTimeOfDays;
        this.itemApplyStats = itemApplyStats;
        this.attackEffects = attackEffects;
    }

    public Stats getItemApplyStats() {
        return itemApplyStats;
    }

    public Set<TimeTracker.TimeOfDay> getValidTimeOfDays() {
        return validTimeOfDays;
    }

    public boolean isDisposable() {
        return isDisposable;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public boolean isWithPlayer() {
        return isWithPlayer;
    }

    public void setWithPlayer(boolean isWithPlayer) {
        if (isWithPlayer) {
            setHasBeenWithPlayer();
        }
        this.isWithPlayer = isWithPlayer;
    }

    public int getNumberOfUses() {
        return numberOfUses;
    }

    public void setNumberOfUses(int numberOfUses) {
        this.numberOfUses = numberOfUses;
    }

    public String getItemId() {
        return itemId;
    }


    public String getInternalItemName() {
        return internalItemName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public List<String> getItemTriggers() {
        return itemTriggers;
    }

    public String getRestingName() {
        return restingName;
    }

    public int getItemHalfLifeTicks() {
        return itemHalfLifeTicks;
    }

    public Loot getLoot() {
        return loot;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getValueInGold() {
        return valueInGold;
    }

    public void setEffects(Set<Effect> effects) {
        this.effects = effects;
    }

    public Set<Effect> getEffects() {
        return effects;
    }

    public void setHasBeenWithPlayer() {
        hasBeenWithPlayer = true;
    }

    public boolean isHasBeenWithPlayer() {
        return hasBeenWithPlayer;
    }

    public Map<Double, Effect> getAttackEffects() {
        return attackEffects;
    }

    public static Item createCorpseItem(String name, Loot loot) {

        Item item = new ItemBuilder()
                .internalItemName(Item.CORPSE_INTENAL_NAME)
                .itemName(name + " corpse")
                .itemDescription("a bloody corpse")
                .itemTriggers(Lists.newArrayList("corpse", "c", name, name + " corpse"))
                .itemId(UUID.randomUUID().toString())
                .itemHalfLifeTicks(120)
                .rarity(Rarity.BASIC)
                .valueInGold(5)
                .isDisposable(false)
                .restingName("a corpse lies on the ground.")
                .loot(loot)
                .create();

        return item;

    }
}
