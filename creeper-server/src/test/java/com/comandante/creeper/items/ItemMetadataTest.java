/*
package com.comandante.creeper.items;

import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.merchant.MerchantItemForSale;
import com.comandante.creeper.merchant.MerchantMetadata;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.spawner.SpawnRule;
import com.comandante.creeper.spawner.SpawnRuleBuilder;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.stats.StatsBuilder;
import com.comandante.creeper.storage.FilebasedJsonStorage;
import com.comandante.creeper.storage.ItemStorage;
import com.comandante.creeper.storage.MerchantStorage;
import com.comandante.creeper.world.model.Area;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.comandante.creeper.server.player_communication.Color.BOLD_ON;


public class ItemMetadataTest {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void testSerialization() throws Exception {
//
//        private String itemName;
//        private String itemDescription;
//        private String restingName;
//        private int numberOfUses;
//        private int valueInGold;
//        private int itemHalfLifeTicks;
//        private Rarity rarity;
//        private Equipment equipment;
//        private Set<Effect> effects;
//        private List<String> itemTriggers;
//
//
//        ItemMetadata itemMetadata = new ItemMetadata();
//        itemMetadata.setInternalItemName("little potion");
//        itemMetadata.setItemName("a " + Color.RED + "little potion" + Color.RESET);
//        itemMetadata.setItemDescription("a " + Color.RED + "little potion" + Color.RESET);
//        itemMetadata.setRestingName("a " + Color.GREEN + "little potion" + Color.RESET);
//        itemMetadata.setMaxUses(1);
//        itemMetadata.setDisposable(true);
//        itemMetadata.setValueInGold(30);
//        itemMetadata.setItemHalfLifeTicks(60);
//        itemMetadata.setRarity(Rarity.BASIC);
//         itemMetadata.setValidTimeOfDays(Sets.newHashSet(TimeTracker.TimeOfDay.MORNING, TimeTracker.TimeOfDay.NIGHT));
//
//        SpawnRule spawnRule1 = new SpawnRuleBuilder().setArea(Area.NEWBIE_ZONE).setSpawnIntervalTicks(600).setMaxInstances(100).setMaxPerRoom(5).setRandomPercent(40).createSpawnRule();
//        SpawnRule spawnRule2 = new SpawnRuleBuilder().setArea(Area.FANCYHOUSE_ZONE).setSpawnIntervalTicks(600).setMaxInstances(12).setMaxPerRoom(2).setRandomPercent(50).createSpawnRule();
//        SpawnRule spawnRule3 = new SpawnRuleBuilder().setArea(Area.HOUSE_ZONE).setSpawnIntervalTicks(600).setMaxInstances(12).setMaxPerRoom(2).setRandomPercent(50).createSpawnRule();
//        itemMetadata.setSpawnRules(Sets.newHashSet(spawnRule1, spawnRule2, spawnRule3));
//
//        Stats stats = new StatsBuilder().setInventorySize(100).createStats();
//          final Equipment equipment = new Equipment(EquipmentSlotType.BAG, stats);
//
//           itemMetadata.setEquipment(equipment);
//        itemMetadata.setItemTriggers(Lists.newArrayList("p", "little potion", "potion"));
//
//
//        EffectBuilder effectBuilder = new EffectBuilder()
//                .setDurationStats(new StatsBuilder().setCurrentHealth(25).createStats())
//                .setEffectApplyMessages(Lists.newArrayList("You start to feel a healing effect."))
//                .setEffectDescription("Little healing.")
//                .setEffectName("Little Potion Heal")
//                .setFrozenMovement(false)
//                .setLifeSpanTicks(0);
//
//
//        itemMetadata.setEffects(Sets.newHashSet(effectBuilder.createEffect()));
//
//
//        ItemStorage itemStorage = new ItemStorage(gson);
//        itemStorage.saveItemMetadata(itemMetadata);
//
//
//        String s = gson.toJson(itemMetadata);
//        System.out.println(s);

    }

    @Test
    public void generator() throws Exception {

        FilebasedJsonStorage filebasedJsonStorage = new FilebasedJsonStorage(new GsonBuilder().setPrettyPrinting().create());
        ItemStorage itemStorage = new ItemStorage(filebasedJsonStorage);

         //BERSERKER BOOTS
        {
            ItemMetadata itemMetadata = metadataFrom(ItemType.BERSEKER_BOOTS);
            Stats stats = new StatsBuilder().setArmorRating(3).createStats();
            final Equipment equipment = new Equipment(EquipmentSlotType.FEET, stats);
            itemMetadata.setEquipment(equipment);
            itemMetadata.setInternalItemName("beserker boots");
            itemStorage.saveItemMetadata(itemMetadata);
        }

        // BERSERKER HELM
        {
            ItemMetadata itemMetadataHelm = metadataFrom(ItemType.BERSEKER_HELM);
            Stats statsHelm = new StatsBuilder().setArmorRating(3).createStats();
            final Equipment equipmentHeml = new Equipment(EquipmentSlotType.HEAD, statsHelm);
            itemMetadataHelm.setEquipment(equipmentHeml);
            itemMetadataHelm.setInternalItemName("beserker helm");
            itemStorage.saveItemMetadata(itemMetadataHelm);
        }


         //BERSERKER SHORTS
        {
            ItemMetadata itemMetadataShorts = metadataFrom(ItemType.BERSEKER_SHORTS);
            Stats statsShorts = new StatsBuilder().setArmorRating(4).createStats();
            final Equipment equipmentShorts = new Equipment(EquipmentSlotType.LEGS, statsShorts);
            itemMetadataShorts.setEquipment(equipmentShorts);
            itemMetadataShorts.setInternalItemName("beserker shorts");
            itemStorage.saveItemMetadata(itemMetadataShorts);
        }


       //  BERSERKER BATON
        {
            ItemMetadata itemMetadataBaton = metadataFrom(ItemType.BERSERKER_BATON);
            Stats statsBaton = new StatsBuilder().setWeaponRatingMin(4).setWeaponRatingMax(6).createStats();
            final Equipment equipmentBaton = new Equipment(EquipmentSlotType.HAND, statsBaton);
            itemMetadataBaton.setEquipment(equipmentBaton);
            itemMetadataBaton.setInternalItemName("beserker baton");
            itemStorage.saveItemMetadata(itemMetadataBaton);
        }

        // BERSERKER BATON
        {
            ItemMetadata itemMetadataBracers = metadataFrom(ItemType.BERSERKER_BRACERS);
            Stats statsBracers = new StatsBuilder().setArmorRating(4).createStats();
            final Equipment equipmentBracers = new Equipment(EquipmentSlotType.WRISTS, statsBracers);
            itemMetadataBracers.setEquipment(equipmentBracers);
            itemMetadataBracers.setInternalItemName("beserker bracers");
            itemStorage.saveItemMetadata(itemMetadataBracers);
        }


        // BERSERKER BATON
        {
            ItemMetadata itemMetadataChest = metadataFrom(ItemType.BERSERKER_CHEST);
            Stats statsChest = new StatsBuilder().setArmorRating(6).createStats();
            final Equipment equipmentChest = new Equipment(EquipmentSlotType.CHEST, statsChest);
            itemMetadataChest.setEquipment(equipmentChest);
            itemMetadataChest.setInternalItemName("beserker chest");
            itemStorage.saveItemMetadata(itemMetadataChest);
        }

        // BIGGERS SKIN SATCHEL
        {
            ItemMetadata itemMetadataBiggersSkinSatchel = metadataFrom(ItemType.BIGGERS_SKIN_SATCHEL);
            Stats statsBiggersSkinSatchel = new StatsBuilder().setInventorySize(100).createStats();
            final Equipment equipmentBiggersSkinSatchel = new Equipment(EquipmentSlotType.BAG, statsBiggersSkinSatchel);
            itemMetadataBiggersSkinSatchel.setEquipment(equipmentBiggersSkinSatchel);
            itemMetadataBiggersSkinSatchel.setInternalItemName("biggers skin satchel");
            itemStorage.saveItemMetadata(itemMetadataBiggersSkinSatchel);
        }

         //Key
        {
            ItemMetadata itemMetadataKey = metadataFrom(ItemType.KEY);
            itemMetadataKey.setInternalItemName("basic key");
            SpawnRule spawnRule = new SpawnRuleBuilder().setArea(Area.LOBBY).setSpawnIntervalTicks(600).setMaxInstances(1).setMaxPerRoom(1).setRandomPercent(5).createSpawnRule();
            itemMetadataKey.setSpawnRules(Sets.newHashSet(spawnRule));
            itemStorage.saveItemMetadata(itemMetadataKey);
        }


        // Marijuana
        {
            ItemMetadata itemMetadataMarijuana = metadataFrom(ItemType.MARIJUANA);
            itemMetadataMarijuana.setInternalItemName("marijuana");
            ForageBuilder marijuanaForageBuilder = new ForageBuilder();
            marijuanaForageBuilder.setMinAmt(1);
            marijuanaForageBuilder.setMaxAmt(3);
            marijuanaForageBuilder.setPctOfSuccess(40);
            marijuanaForageBuilder.setForageExperience(4);
            marijuanaForageBuilder.setCoolDownTicks(600);
            marijuanaForageBuilder.setAreas(Sets.newHashSet(Area.WESTERN9_ZONE, Area.NORTH3_ZONE, Area.BLOODRIDGE2_ZONE, Area.BLOODRIDGE1_ZONE));
            itemMetadataMarijuana.setForages(Sets.newHashSet(marijuanaForageBuilder.createForage()));
            itemStorage.saveItemMetadata(itemMetadataMarijuana);
        }

        // Drank
        {
            ItemMetadata itemMetadataDrank = metadataFrom(ItemType.PURPLE_DRANK);
            itemMetadataDrank.setInternalItemName("purple drank");
            Stats statsDrank = new StatsBuilder().setCurrentHealth(50).createStats();
            itemMetadataDrank.setItemApplyStats(statsDrank);
            itemStorage.saveItemMetadata(itemMetadataDrank);
        }

        // Smell Health Potion
        {
            Set<SpawnRule> spawnRulesHealthPotion = Sets.newHashSet(
                    new SpawnRuleBuilder().setArea(Area.NEWBIE_ZONE).setSpawnIntervalTicks(600).setMaxInstances(100).setMaxPerRoom(5).setRandomPercent(40).createSpawnRule(),
                    new SpawnRuleBuilder().setArea(Area.FANCYHOUSE_ZONE).setSpawnIntervalTicks(600).setMaxInstances(12).setMaxPerRoom(2).setRandomPercent(50).createSpawnRule(),
                    new SpawnRuleBuilder().setArea(Area.HOUSE_ZONE).setSpawnIntervalTicks(600).setMaxInstances(12).setMaxPerRoom(2).setRandomPercent(50).createSpawnRule()
            );
            ItemMetadata itemMetadataHealthPotion = metadataFrom(ItemType.SMALL_HEALTH_POTION);
            itemMetadataHealthPotion.setInternalItemName("small health potion");
            Stats statsHealthPotion = new StatsBuilder().setCurrentHealth(20).createStats();
            itemMetadataHealthPotion.setSpawnRules(spawnRulesHealthPotion);
            itemMetadataHealthPotion.setItemApplyStats(statsHealthPotion);
            itemStorage.saveItemMetadata(itemMetadataHealthPotion);
        }

        {

            //  BIGGERS SKIN SATCHEL
            ItemMetadata itemMetadataSpellbook = metadataFrom(ItemType.LIGHTNING_SPELLBOOKNG);
            itemMetadataSpellbook.setInternalItemName("lightning spellbook");
            itemStorage.saveItemMetadata(itemMetadataSpellbook);
        }

        {
            // Stick OF Justice
            ItemMetadata itemMetadataStickOfJustice = metadataFrom(ItemType.STICK_OF_JUSTICE);
            itemMetadataStickOfJustice.setInternalItemName("stick of justice");
            itemStorage.saveItemMetadata(itemMetadataStickOfJustice);
        }


        {
            // RED CLAW BEANIE
            ItemMetadata itemMetadataBeanie = metadataFrom(ItemType.RED_CLAW_BEANIE);
            Stats statsBeanie = new StatsBuilder().setArmorRating(8).setStrength(4).setMaxHealth(50).createStats();
            final Equipment equipmentBeanie = new Equipment(EquipmentSlotType.HEAD, statsBeanie);
            itemMetadataBeanie.setEquipment(equipmentBeanie);
            itemMetadataBeanie.setInternalItemName("red claw beanie");
            itemStorage.saveItemMetadata(itemMetadataBeanie);
        }

        {
            // RED CLAW Hoodie
            ItemMetadata itemMetadataHoodie = metadataFrom(ItemType.RED_CLAW_HOODIE);
            Stats statsHoodie = new StatsBuilder().setArmorRating(15).setStrength(7).createStats();
            final Equipment equipmentHoodie = new Equipment(EquipmentSlotType.CHEST, statsHoodie);
            itemMetadataHoodie.setEquipment(equipmentHoodie);
            itemMetadataHoodie.setInternalItemName("rad claw hoodie");
            itemStorage.saveItemMetadata(itemMetadataHoodie);
        }

        {
            //   RED CLAW PANTS
            ItemMetadata itemMetadataPants = metadataFrom(ItemType.RED_CLAW_PANTS);
            Stats statsPants = new StatsBuilder().setArmorRating(15).setStrength(7).createStats();
            final Equipment equipmentPants = new Equipment(EquipmentSlotType.LEGS, statsPants);
            itemMetadataPants.setEquipment(equipmentPants);
            itemMetadataPants.setInternalItemName("rad claw pants");
            itemStorage.saveItemMetadata(itemMetadataPants);
            //Lether Satchel
        }

        {
            ItemMetadata itemMetadataPants = metadataFrom(ItemType.LEATHER_SATCHEL);
            Stats stats = new StatsBuilder().setInventorySize(15).createStats();
            final Equipment equipment = new Equipment(EquipmentSlotType.BAG, stats);
            itemMetadataPants.setEquipment(equipment);
            itemMetadataPants.setInternalItemName("leather satchel");
            itemStorage.saveItemMetadata(itemMetadataPants);
        }
    }


    private ItemMetadata metadataFrom(ItemType itemType) {
        ItemMetadata itemMetadata = new ItemMetadata();
        itemMetadata.setInternalItemName(null);
        itemMetadata.setItemName(itemType.getItemName());
        itemMetadata.setRestingName(itemType.getRestingName());
        itemMetadata.setItemDescription(itemType.getItemDescription());
        itemMetadata.setItemHalfLifeTicks(itemType.getItemHalfLifeTicks());
        itemMetadata.setItemTriggers(itemType.getItemTriggers());
        itemMetadata.setMaxUses(itemType.getMaxUses());
        itemMetadata.setRarity(itemType.getRarity());
        itemMetadata.setSpawnRules(null);
        itemMetadata.setValidTimeOfDays(itemType.getValidTimeOfDays());
        itemMetadata.setValueInGold(itemType.getValueInGold());
        itemMetadata.setItemApplyStats(null);
        itemMetadata.setEquipment(null);
        itemMetadata.setEffects(null);
        itemMetadata.setDisposable(itemType.isDisposable());
        return itemMetadata;
    }


    @Test
    public void generateMerchants() throws Exception {

        // LLOYD BARTENDER
        {
            final String welcomeMessage = " _        _        _______           ______   _  _______ \r\n" +
                    "( \\      ( \\      (  ___  )|\\     /|(  __  \\ ( )(  ____ \\\r\n" +
                    "| (      | (      | (   ) |( \\   / )| (  \\  )|/ | (    \\/\r\n" +
                    "| |      | |      | |   | | \\ (_) / | |   ) |   | (_____ \r\n" +
                    "| |      | |      | |   | |  \\   /  | |   | |   (_____  )\r\n" +
                    "| |      | |      | |   | |   ) (   | |   ) |         ) |\r\n" +
                    "| (____/\\| (____/\\| (___) |   | |   | (__/  )   /\\____) |\r\n" +
                    "(_______/(_______/(_______)   \\_/   (______/    \\_______)\r\n" +
                    "                                                         ";


            String name = "lloyd the bartender";
            String colorName = BOLD_ON + Color.CYAN + name + Color.RESET;
            Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"lloyd", "bartender", "barkeep", "Lloyd", "LLOYD", name}));

            List<MerchantItemForSale> itemsForSale = Lists.newArrayList();
            itemsForSale.add(new MerchantItemForSale("small health potion", 8));
            itemsForSale.add(new MerchantItemForSale("purple drank", 80));
            itemsForSale.add(new MerchantItemForSale("biggers skin satchel", 25000));
            MerchantMetadata merchantMetadata = new MerchantMetadata();
            merchantMetadata.setName(name);
            merchantMetadata.setInternalName(name);
            merchantMetadata.setColorName(colorName);
            merchantMetadata.setMerchantItemForSales(itemsForSale);
            merchantMetadata.setRoomIds(Sets.newHashSet(64));
            merchantMetadata.setValidTriggers(validTriggers);
            merchantMetadata.setWelcomeMessage(welcomeMessage);

            FilebasedJsonStorage filebasedJsonStorage = new FilebasedJsonStorage(new GsonBuilder().setPrettyPrinting().create());
            MerchantStorage merchantStorage = new MerchantStorage(null, filebasedJsonStorage);
            merchantStorage.saveMerchantMetadata(merchantMetadata);

        }

        // BIGGER BLACKSMITH
        {
            final String welcomeMessage = "  ____  _                                              \n" +
                    "\" +\n" +
                    "                    \" | __ )(_) __ _  __ _  ___ _ __ ___                    \\r\\n\" +\n" +
                    "                    \" |  _ \\\\| |/ _` |/ _` |/ _ \\\\ '__/ __|                   \\r\\n\" +\n" +
                    "                    \" | |_) | | (_| | (_| |  __/ |  \\\\__ \\\\                   \\r\\n\" +\n" +
                    "                    \" |____/|_|\\\\__, |\\\\__, |\\\\___|_|  |___/                   \\r\\n\" +\n" +
                    "                    \"     ____ |___/ |___/   _                  _ _   _     \\r\\n\" +\n" +
                    "                    \"    | __ )| | __ _  ___| | _____ _ __ ___ (_) |_| |__  \\r\\n\" +\n" +
                    "                    \"    |  _ \\\\| |/ _` |/ __| |/ / __| '_ ` _ \\\\| | __| '_ \\\\ \\r\\n\" +\n" +
                    "                    \"    | |_) | | (_| | (__|   <\\\\__ \\\\ | | | | | | |_| | | |\\r\\n\" +\n" +
                    "                    \"    |____/|_|\\__,_|\\___|_|\\_\\___/_| |_| |_|_|\\__|_| |_|\n";


            String name = "biggers the blacksmith";
            String colorName = BOLD_ON + Color.CYAN + name + Color.RESET;
            Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"blacksmith", "biggers the blacksmith", "biggers", name}));

            List<MerchantItemForSale> blacksmithItems = Lists.newArrayList();
            blacksmithItems.add(new MerchantItemForSale("berserker baton", 10000));
            blacksmithItems.add(new MerchantItemForSale("berserker boots", 3500));
            blacksmithItems.add(new MerchantItemForSale("berserker bracers", 3500));
            blacksmithItems.add(new MerchantItemForSale("berserker helm", 3500));
            blacksmithItems.add(new MerchantItemForSale("berserker chest", 7000));
            blacksmithItems.add(new MerchantItemForSale("berserker shorts", 8500));
            blacksmithItems.add(new MerchantItemForSale("leather satchel", 600));
            MerchantMetadata merchantMetadata = new MerchantMetadata();
            merchantMetadata.setName(name);
            merchantMetadata.setInternalName(name);
            merchantMetadata.setColorName(colorName);
            merchantMetadata.setMerchantItemForSales(blacksmithItems);
            merchantMetadata.setRoomIds(Sets.newHashSet(66, 253));
            merchantMetadata.setValidTriggers(validTriggers);
            merchantMetadata.setWelcomeMessage(welcomeMessage);
            FilebasedJsonStorage filebasedJsonStorage = new FilebasedJsonStorage(new GsonBuilder().setPrettyPrinting().create());
            MerchantStorage merchantStorage = new MerchantStorage(null, filebasedJsonStorage);
            merchantStorage.saveMerchantMetadata(merchantMetadata);
        }

        {
            String name = "nigel the bartender";
            String colorName = BOLD_ON + Color.CYAN + name + Color.RESET;
            Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"nigel", "bartender", "barkeep", "Nigel", "NIGEL", name}));

            List<MerchantItemForSale> blacksmithItems = Lists.newArrayList();
            blacksmithItems.add(new MerchantItemForSale("small health potion", 6));
            MerchantMetadata merchantMetadata = new MerchantMetadata();
            merchantMetadata.setName(name);
            merchantMetadata.setInternalName(name);
            merchantMetadata.setColorName(colorName);
            merchantMetadata.setMerchantItemForSales(blacksmithItems);
            merchantMetadata.setRoomIds(Sets.newHashSet(377));
            merchantMetadata.setValidTriggers(validTriggers);
            merchantMetadata.setWelcomeMessage("\r\n N I G E L 'S   B A R \r\n");
            FilebasedJsonStorage filebasedJsonStorage = new FilebasedJsonStorage(new GsonBuilder().setPrettyPrinting().create());
            MerchantStorage merchantStorage = new MerchantStorage(null, filebasedJsonStorage);
            merchantStorage.saveMerchantMetadata(merchantMetadata);

        }

        {
        String name = "willy the wizard";
        String colorName = BOLD_ON + Color.CYAN + name + Color.RESET;
        Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                {"wizard", "willy the wizard", "willy", name}));

        List<MerchantItemForSale> items = Lists.newArrayList();
        items.add(new MerchantItemForSale("lightning spellbook", 50000));
        MerchantMetadata merchantMetadata = new MerchantMetadata();
        merchantMetadata.setName(name);
        merchantMetadata.setInternalName(name);
        merchantMetadata.setColorName(colorName);
        merchantMetadata.setMerchantItemForSales(items);
        merchantMetadata.setRoomIds(Sets.newHashSet(98));
        merchantMetadata.setValidTriggers(validTriggers);
            String welcomeMessage = "  ____                                        \n" +
                    " 6MMMMb\\                                      \n" +
                    "6M'    `   /                                  \n" +
                    "MM        /M       _____     _____   __ ____  \n" +
                    "YM.      /MMMMM   6MMMMMb   6MMMMMb  `M6MMMMb \n" +
                    " YMMMMb   MM     6M'   `Mb 6M'   `Mb  MM'  `Mb\n" +
                    "     `Mb  MM     MM     MM MM     MM  MM    MM\n" +
                    "      MM  MM     MM     MM MM     MM  MM    MM\n" +
                    "      MM  MM     MM     MM MM     MM  MM    MM\n" +
                    "L    ,M9  YM.  , YM.   ,M9 YM.   ,M9  MM.  ,M9\n" +
                    "MYMMMM9    YMMM9  YMMMMM9   YMMMMM9   MMYMMM9 \n" +
                    "                                      MM      \n" +
                    "                                      MM      \n" +
                    "                                     _MM_     \n" +
                    "\n";
        merchantMetadata.setWelcomeMessage(welcomeMessage);
        FilebasedJsonStorage filebasedJsonStorage = new FilebasedJsonStorage(new GsonBuilder().setPrettyPrinting().create());
        MerchantStorage merchantStorage = new MerchantStorage(null, filebasedJsonStorage);
        merchantStorage.saveMerchantMetadata(merchantMetadata);

        }



        {
            String welcomeMessage = "Welcome to the First National Bank of Creeper.";
            final String name = "jim the banker";
            final String colorName = BOLD_ON + Color.CYAN + name + Color.RESET;
            Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"bank", "banker", "jim the banker", "jim", "j", name}));
            MerchantMetadata merchantMetadata = new MerchantMetadata();
            merchantMetadata.setWelcomeMessage(welcomeMessage);
            merchantMetadata.setName(name);
            merchantMetadata.setInternalName(name);
            merchantMetadata.setColorName(colorName);
            merchantMetadata.setValidTriggers(validTriggers);
            merchantMetadata.setMerchantType(Merchant.MerchantType.BANK);
            merchantMetadata.setRoomIds(Sets.newHashSet(65, 209));
            FilebasedJsonStorage filebasedJsonStorage = new FilebasedJsonStorage(new GsonBuilder().setPrettyPrinting().create());
            MerchantStorage merchantStorage = new MerchantStorage(null, filebasedJsonStorage);
            merchantStorage.saveMerchantMetadata(merchantMetadata);
        }

        {
            final String name = "old wise man";
            final String colorName = BOLD_ON + Color.CYAN + name + Color.RESET;
            String welcomeMessage = "The " + colorName + " can assist you in choosing a character class.\r\n";
            Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"wise", "man", "old", "old wise man", "m", "w", name}));
            MerchantMetadata merchantMetadata = new MerchantMetadata();
            merchantMetadata.setWelcomeMessage(welcomeMessage);
            merchantMetadata.setName(name);
            merchantMetadata.setInternalName(name);
            merchantMetadata.setColorName(colorName);
            merchantMetadata.setValidTriggers(validTriggers);
            merchantMetadata.setMerchantType(Merchant.MerchantType.PLAYERCLASS_SELECTOR);
            merchantMetadata.setRoomIds(Sets.newHashSet(2));
            FilebasedJsonStorage filebasedJsonStorage = new FilebasedJsonStorage(new GsonBuilder().setPrettyPrinting().create());
            MerchantStorage merchantStorage = new MerchantStorage(null, filebasedJsonStorage);
            merchantStorage.saveMerchantMetadata(merchantMetadata);
        }

        {
            final String name = "a bank of lockers";
            final String colorName = BOLD_ON + Color.CYAN + name + Color.RESET;
            String welcomeMessage = "Locker opened.";
            Set<String> validTriggers = new HashSet<String>(Arrays.asList(new String[]
                    {"lockers", "locker", "l", name}));
            MerchantMetadata merchantMetadata = new MerchantMetadata();
            merchantMetadata.setWelcomeMessage(welcomeMessage);
            merchantMetadata.setName(name);
            merchantMetadata.setInternalName(name);
            merchantMetadata.setColorName(colorName);
            merchantMetadata.setValidTriggers(validTriggers);
            merchantMetadata.setMerchantType(Merchant.MerchantType.LOCKER);
            merchantMetadata.setRoomIds(Sets.newHashSet(63));
            FilebasedJsonStorage filebasedJsonStorage = new FilebasedJsonStorage(new GsonBuilder().setPrettyPrinting().create());
            MerchantStorage merchantStorage = new MerchantStorage(null, filebasedJsonStorage);
            merchantStorage.saveMerchantMetadata(merchantMetadata);

        }

    }

}
*/
