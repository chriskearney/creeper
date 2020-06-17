package com.comandante.creeper.items;


import com.comandante.creeper.core_game.service.TimeOfDay;
import com.comandante.creeper.stats.Stats;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item implements Serializable {

    private String itemName;
    private String itemDescription;
    private String internalItemName;
    private List<String> itemTriggers;
    private String restingName;
    private String itemId;
    private int numberOfUses;
    private boolean isWithPlayer;
    private Loot loot;
    private int itemHalfLifeTicks;
    private Equipment equipment;
    private Rarity rarity;
    private int valueInGold;
    private Set<Effect> effects;
    private boolean hasBeenWithPlayer;
    private int maxUses;
    private boolean isDisposable;
    private Set<TimeOfDay> validTimeOfDays;
    private Stats itemApplyStats;
    private Map<Double, Effect> attackEffects;
    private boolean isChest;
    private LockPickingDifficulty lockPickingDifficulty;

    public static String CORPSE_INTENAL_NAME = "corpse";

    public Item() {
    }

    protected Item(String itemName,
                   String itemDescription,
                   String internalItemName,
                   List<String> itemTriggers,
                   String restingName,
                   String itemId,
                   int numberOfUses,
                   boolean isWithPlayer,
                   Loot loot,
                   int itemHalfLifeTicks,
                   Equipment equipment,
                   Rarity rarity,
                   int valueInGold,
                   Set<Effect> effects,
                   boolean hasBeenWithPlayer,
                   int maxUses,
                   boolean isDisposable,
                   Set<TimeOfDay> validTimeOfDays,
                   Stats itemApplyStats,
                   Map<Double, Effect> attackEffects,
                   boolean isChest,
                   LockPickingDifficulty lockPickingDifficulty) {
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
        this.isChest = isChest;
        this.lockPickingDifficulty = lockPickingDifficulty;
    }

    public Stats getItemApplyStats() {
        return itemApplyStats;
    }

    public Set<TimeOfDay> getValidTimeOfDays() {
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

    public boolean isChest() {
        return isChest;
    }

    public LockPickingDifficulty getLockPickingDifficulty() {
        return lockPickingDifficulty;
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
