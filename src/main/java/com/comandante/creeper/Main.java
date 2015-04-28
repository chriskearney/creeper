package com.comandante.creeper;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.PickledGraphite;
import com.comandante.creeper.Items.Item;
import com.comandante.creeper.Items.ItemType;
import com.comandante.creeper.Items.Loot;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.managers.SessionManager;
import com.comandante.creeper.merchant.LloydBartender;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.merchant.MerchantItemForSale;
import com.comandante.creeper.npc.*;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.CreeperCommandRegistry;
import com.comandante.creeper.server.CreeperServer;
import com.comandante.creeper.server.command.*;
import com.comandante.creeper.server.command.admin.*;
import com.comandante.creeper.spawner.ItemSpawner;
import com.comandante.creeper.spawner.NpcSpawner;
import com.comandante.creeper.spawner.SpawnRule;
import com.comandante.creeper.world.Area;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.RoomManager;
import com.comandante.creeper.world.WorldExporter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {

    public static CreeperCommandRegistry creeperCommandRegistry;
    private static final Logger log = Logger.getLogger(Main.class);

    private static final int PORT = 8080;
    public static final String MUD_NAME = "creeper";

    final public static MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws Exception {

        final JmxReporter jmxReporter = JmxReporter.forRegistry(metrics).build();
        jmxReporter.start();

        final PickledGraphite pickledGraphite = new PickledGraphite(new InetSocketAddress("192.168.1.11", 2004));
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
                .prefixedWith(MUD_NAME)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(pickledGraphite);
        reporter.start(1, TimeUnit.MINUTES);

        checkAndCreateWorld();

        DB db = DBMaker.newFileDB(new File("world/creeper.mapdb")).closeOnJvmShutdown().encryptionEnable("creepandicrawl").make();

        RoomManager roomManager = new RoomManager();
        PlayerManager playerManager = new PlayerManager(db, new SessionManager());
        startUpMessage("Configuring default admins.");
        MapsManager mapsManager = new MapsManager(roomManager);
        ChannelUtils channelUtils = new ChannelUtils(playerManager, roomManager);
        EntityManager entityManager = new EntityManager(roomManager, playerManager, db, channelUtils);
        GameManager gameManager = new GameManager(roomManager, playerManager, entityManager, mapsManager, channelUtils);
        startUpMessage("Reading world from disk.");
        WorldExporter worldExporter = new WorldExporter(roomManager, mapsManager, gameManager.getFloorManager(), entityManager);
        worldExporter.readWorldFromDisk();
        startUpMessage("Generating maps");
        mapsManager.generateAllMaps(14, 14);

        startUpMessage("Configuring Creeper Commmands");
        creeperCommandRegistry = new CreeperCommandRegistry(new UnknownCommand(gameManager));
        creeperCommandRegistry.addCommand(new DropCommand(gameManager));
        creeperCommandRegistry.addCommand(new GossipCommand(gameManager));
        creeperCommandRegistry.addCommand(new InventoryCommand(gameManager));
        creeperCommandRegistry.addCommand(new FightKillCommand(gameManager));
        creeperCommandRegistry.addCommand(new LookCommand(gameManager));
        creeperCommandRegistry.addCommand(new MovementCommand(gameManager));
        creeperCommandRegistry.addCommand(new PickUpCommand(gameManager));
        creeperCommandRegistry.addCommand(new SayCommand(gameManager));
        creeperCommandRegistry.addCommand(new TellCommand(gameManager));
        creeperCommandRegistry.addCommand(new UseCommand(gameManager));
        creeperCommandRegistry.addCommand(new WhoamiCommand(gameManager));
        creeperCommandRegistry.addCommand(new WhoCommand(gameManager));
        creeperCommandRegistry.addCommand(new DescriptionCommand(gameManager));
        creeperCommandRegistry.addCommand(new TitleCommand(gameManager));
        creeperCommandRegistry.addCommand(new TagRoomCommand(gameManager));
        creeperCommandRegistry.addCommand(new SaveWorldCommand(gameManager));
        creeperCommandRegistry.addCommand(new BuildCommand(gameManager));
        creeperCommandRegistry.addCommand(new MapCommand(gameManager));
        creeperCommandRegistry.addCommand(new AreaCommand(gameManager));
        creeperCommandRegistry.addCommand(new HelpCommand(gameManager));
        creeperCommandRegistry.addCommand(new LootCommand(gameManager));
        creeperCommandRegistry.addCommand(new GoldCommand(gameManager));
        creeperCommandRegistry.addCommand(new InfoCommand(gameManager));
        creeperCommandRegistry.addCommand(new TeleportCommand(gameManager));
        creeperCommandRegistry.addCommand(new TalkCommand(gameManager));


        createNpcs(entityManager, gameManager);

        CreeperServer creeperServer = new CreeperServer(PORT, db);
        startUpMessage("Creeper engine started");
        creeperServer.run(gameManager);
        startUpMessage("Creeper online");
    }

    private static void startUpMessage(String message) {
        //System.out.println("[STARTUP] " + message);
        log.info(message);
    }

    private static void checkAndCreateWorld() throws IOException {
        if (!Files.isDirectory().apply(new File("world/"))) {
            Files.createParentDirs(new File("world/creeper_world_stuff"));
        }
    }

    public static String createPlayerId(String playerName) {
        return new String(Base64.encodeBase64(playerName.getBytes()));
    }

    private static void createNpcs(EntityManager entityManager, GameManager gameManager) {


        startUpMessage("Adding Street Hustlers");
        entityManager.addEntity(new NpcSpawner(new StreetHustler(gameManager, new Loot(1, 3, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NEWBIE_ZONE), gameManager, new SpawnRule(10, 5, 3, 100)));

        startUpMessage("Adding beer");
        ItemSpawner itemSpawner = new ItemSpawner(ItemType.BEER, Area.NEWBIE_ZONE, new SpawnRule(10, 50, 2, 25), gameManager);

        entityManager.addEntity(itemSpawner);

        startUpMessage("Adding Tree Berserkers");
        entityManager.addEntity(new NpcSpawner(new TreeBerserker(gameManager, new Loot(2, 5, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NEWBIE_ZONE, Area.NORTH1_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));

        startUpMessage("Adding Swamp Berserkers");
        entityManager.addEntity(new NpcSpawner(new SwampBerserker(gameManager, new Loot(4, 10, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NORTH2_ZONE), gameManager, new SpawnRule(10, 8, 2, 100)));

        startUpMessage("Adding Berg Orcs");
        entityManager.addEntity(new NpcSpawner(new BergOrc(gameManager, new Loot(4, 10, Sets.<Item>newHashSet())), Sets.newHashSet(Area.BLOODRIDGE1_ZONE), gameManager, new SpawnRule(10, 8, 2, 100)));

        startUpMessage("Adding Red-Eyed Bears");
        entityManager.addEntity(new NpcSpawner(new RedEyedBear(gameManager, new Loot(8, 13, Sets.<Item>newHashSet())), Sets.newHashSet(Area.TOFT1_ZONE), gameManager, new SpawnRule(10, 14, 3, 100)));
        entityManager.addEntity(new NpcSpawner(new RedEyedBear(gameManager, new Loot(8, 13, Sets.<Item>newHashSet())), Sets.newHashSet(Area.TOFT2_ZONE), gameManager, new SpawnRule(10, 14, 3, 100)));

        startUpMessage("Adding Swamp Bears");
        entityManager.addEntity(new NpcSpawner(new SwampBear(gameManager, new Loot(9, 14, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NORTH3_ZONE), gameManager, new SpawnRule(10, 12, 3, 100)));
        entityManager.addEntity(new NpcSpawner(new SwampBear(gameManager, new Loot(9, 14, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NORTH4_ZONE), gameManager, new SpawnRule(10, 12, 3, 100)));

        startUpMessage("Adding Gray Ekimmus");
        entityManager.addEntity(new NpcSpawner(new GrayEkimmu(gameManager, new Loot(14, 17, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NORTH4_ZONE), gameManager, new SpawnRule(10, 12, 3, 100)));
        entityManager.addEntity(new NpcSpawner(new GrayEkimmu(gameManager, new Loot(14, 17, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NORTH5_ZONE), gameManager, new SpawnRule(10, 12, 3, 100)));

        startUpMessage("Adding Stealth Panthers");
        entityManager.addEntity(new NpcSpawner(new StealthPanther(gameManager, new Loot(14, 22, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NORTH5_ZONE), gameManager, new SpawnRule(10, 12, 3, 100)));
        entityManager.addEntity(new NpcSpawner(new StealthPanther(gameManager, new Loot(14, 22, Sets.<Item>newHashSet())), Sets.newHashSet(Area.NORTH6_ZONE), gameManager, new SpawnRule(10, 12, 3, 100)));

        startUpMessage("Adding Phantom Knights");

        PhantomKnight phantomKnight = new PhantomKnight(gameManager, new Loot(18, 26, Sets.<Item>newHashSet()));
        entityManager.addEntity(new NpcSpawner(phantomKnight, Sets.newHashSet(Area.BLOODRIDGE5_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));
        entityManager.addEntity(new NpcSpawner(phantomKnight, Sets.newHashSet(Area.BLOODRIDGE6_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));
        entityManager.addEntity(new NpcSpawner(phantomKnight, Sets.newHashSet(Area.BLOODRIDGE7_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));


        PhantomOrc phantomOrc = new PhantomOrc(gameManager, new Loot(16, 24, Sets.<Item>newHashSet()));
        startUpMessage("Adding Phantom Orcs");
        entityManager.addEntity(new NpcSpawner(phantomOrc, Sets.newHashSet(Area.BLOODRIDGE4_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));
        entityManager.addEntity(new NpcSpawner(phantomOrc, Sets.newHashSet(Area.BLOODRIDGE4_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));

        PhantomWizard phantomWizard = new PhantomWizard(gameManager, new Loot(16, 24, Sets.<Item>newHashSet()));
        startUpMessage("Adding Phantom Wizards");
        entityManager.addEntity(new NpcSpawner(phantomWizard, Sets.newHashSet(Area.BLOODRIDGE4_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));
        entityManager.addEntity(new NpcSpawner(phantomWizard, Sets.newHashSet(Area.BLOODRIDGE5_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));

        startUpMessage("Adding Demon Succubi");
        DemonSuccubus demonSuccubus = new DemonSuccubus(gameManager, new Loot(80, 100, Sets.<Item>newHashSet()));
        entityManager.addEntity(new NpcSpawner(demonSuccubus, Sets.newHashSet(Area.WESTERN9_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));
        entityManager.addEntity(new NpcSpawner(demonSuccubus, Sets.newHashSet(Area.WESTERN10_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));

        startUpMessage("Adding Death Griffins");
        DeathGriffin deathGriffin = new DeathGriffin(gameManager, new Loot(60, 80, Sets.<Item>newHashSet()));
        entityManager.addEntity(new NpcSpawner(deathGriffin, Sets.newHashSet(Area.WESTERN8_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));
        entityManager.addEntity(new NpcSpawner(deathGriffin, Sets.newHashSet(Area.WESTERN9_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));

        startUpMessage("Adding Nightmare Trolls");
        NightmareTroll nightmareTroll = new NightmareTroll(gameManager, new Loot(18, 26, Sets.<Item>newHashSet()));
        entityManager.addEntity(new NpcSpawner(nightmareTroll, Sets.newHashSet(Area.NORTH7_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));
        entityManager.addEntity(new NpcSpawner(nightmareTroll, Sets.newHashSet(Area.NORTH8_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));

        startUpMessage("Adding Tiger Monoceruses");
        TigerMonocerus tigerMonocerus = new TigerMonocerus(gameManager, new Loot(20, 29, Sets.<Item>newHashSet()));
        entityManager.addEntity(new NpcSpawner(tigerMonocerus, Sets.newHashSet(Area.NORTH8_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));
        entityManager.addEntity(new NpcSpawner(tigerMonocerus, Sets.newHashSet(Area.NORTH9_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));

        startUpMessage("Adding Razor-claw Wolves");
        RazorClawWolf razorClawWolf = new RazorClawWolf(gameManager, new Loot(18, 26, Sets.<Item>newHashSet()));
        entityManager.addEntity(new NpcSpawner(razorClawWolf, Sets.newHashSet(Area.TOFT2_ZONE), gameManager, new SpawnRule(10, 6, 2, 100)));
        entityManager.addEntity(new NpcSpawner(razorClawWolf, Sets.newHashSet(Area.TOFT3_ZONE), gameManager, new SpawnRule(10, 14, 2, 100)));

        Map<Integer, MerchantItemForSale> itemsForSale = Maps.newHashMap();
        MerchantItemForSale merchantItemForSale = new MerchantItemForSale(ItemType.BEER, 2);
        itemsForSale.put(1, merchantItemForSale);
        LloydBartender lloydBartender = new LloydBartender(gameManager, new Loot(18, 26, Sets.<Item>newHashSet()), itemsForSale);
        gameManager.getRoomManager().addMerchant(64, lloydBartender);

    }
}
