package com.comandante.creeper;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.PickledGraphite;
import com.comandante.creeper.Items.ItemUseRegistry;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.player.ExperienceManager;
import com.comandante.creeper.player.PlayerManagementManager;
import com.comandante.creeper.managers.GameManager;
import com.comandante.creeper.managers.SessionManager;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.CreeperServer;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.RoomManager;
import com.comandante.creeper.world.WorldExporter;
import com.google.common.io.Files;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.*;
import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

public class Main {

    private static final Logger log = Logger.getLogger(Main.class);

    final public static MetricRegistry metrics = new MetricRegistry();

    final public static Set<Character> vowels = new HashSet<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u'));

    public static String getCreeperVersion() {
        Properties props = new Properties();
        try {
            props.load(Main.class.getResourceAsStream("/build.properties"));
        } catch (Exception e) {
            log.error("Problem reading build properties file.", e);
            return "0";
        }
        String buildVersion = props.getProperty("build.version");
        String timestampString = props.getProperty("build.timestamp");
        if (buildVersion == null || timestampString == null || timestampString.isEmpty() || buildVersion.isEmpty()) {
            return "creeper-local-dev";
        }
        long buildTimestamp = Long.parseLong(timestampString);
        Date date = new Date(buildTimestamp);
        SimpleDateFormat format = new SimpleDateFormat();
        String dateFormatted = format.format(date);

        return buildVersion + " " + dateFormatted;
    }
    public static void main(String[] args) throws Exception {

        CreeperConfiguration creeperConfiguration = new CreeperConfiguration(buildConfiguration(args));

        final JmxReporter jmxReporter = JmxReporter.forRegistry(metrics).build();
        jmxReporter.start();

        if (creeperConfiguration.isProduction) {
            final PickledGraphite pickledGraphite = new PickledGraphite(new InetSocketAddress(creeperConfiguration.graphiteHostname, creeperConfiguration.graphitePort));
            final GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
                    .prefixedWith(creeperConfiguration.mudName)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .build(pickledGraphite);
            reporter.start(1, TimeUnit.MINUTES);
        }

        Files.isDirectory().apply(new File("world/"));

        DB db = DBMaker.newFileDB(new File("world/" + creeperConfiguration.databaseFileName)).closeOnJvmShutdown().make();

        PlayerManager playerManager = new PlayerManager(db, new SessionManager());
        playerManager.createAllGauges();

        RoomManager roomManager = new RoomManager(playerManager);

        startUpMessage("Configuring core systems.");
        MapsManager mapsManager = new MapsManager(creeperConfiguration, roomManager);
        ChannelUtils channelUtils = new ChannelUtils(playerManager, roomManager);
        EntityManager entityManager = new EntityManager(roomManager, playerManager, db);
        GameManager gameManager = new GameManager(creeperConfiguration, roomManager, playerManager, entityManager, mapsManager, channelUtils);

        startUpMessage("Reading world from disk.");
        WorldExporter worldExporter = new WorldExporter(roomManager, mapsManager, gameManager.getFloorManager(), entityManager, gameManager);
        worldExporter.readWorldFromDisk();

        startUpMessage("Creating and registering Player Management MBeans.");
        gameManager.getPlayerManagementManager().processPlayersMarkedForDeletion();
        gameManager.getPlayerManagementManager().createAndRegisterAllPlayerManagementMBeans();

        startUpMessage("Configuring commands");
        ConfigureCommands.configure(gameManager);

        startUpMessage("Configure Bank commands");
        ConfigureCommands.configureBankCommands(gameManager);

        startUpMessage("Configure Locker commands");
        ConfigureCommands.configureLockerCommands(gameManager);

        startUpMessage("Configuring npcs and merchants");
        ConfigureNpc.configure(entityManager, gameManager);
        CreeperServer creeperServer = new CreeperServer(creeperConfiguration.telnetPort);

        startUpMessage("Generating map data.");
        mapsManager.generateAllMaps();

        startUpMessage("Configuring Item Use Registry");
        ItemUseRegistry.configure();

        startUpMessage("Configuring default inventorySize limits");
        BackportCommands.configureDefaultInventorySize(entityManager, gameManager);

        startUpMessage("Configuring default maxEffects limits");
        BackportCommands.configureDefaultMaxEffectSize(entityManager, gameManager);

        BackportCommands.configureFibsHealth(entityManager, gameManager);

        ExperienceManager.calculateXpRanges();
        startUpMessage("Calculated XP Ranges");

        startUpMessage("Creeper MUD engine started");

        creeperServer.run(gameManager);
        startUpMessage("Creeper MUD engine online");

        if (creeperConfiguration.isIrcEnabled) {
            startUpMessage("Starting irc server.");
            configureIrc(gameManager);
        }

        MBeanRefresherService mBeanRefresherService = new MBeanRefresherService(gameManager);
        mBeanRefresherService.startAsync();
        startUpMessage("MBean Refresher Service started.");
    }

    public static void startUpMessage(String message) {
        log.info(message);
    }

    public static String createPlayerId(String playerName) {
        return new String(Base64.encodeBase64(playerName.getBytes()));
    }

    private static Configuration buildConfiguration(String[] args) throws ConfigurationException {
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
        File propFile;
        if (args.length == 0) {
            propFile = new File("server.config");
        } else {
            propFile = new File(args[0]);
        }
        if (propFile.exists()) {
            compositeConfiguration.addConfiguration(new PropertiesConfiguration(propFile));
        }
        compositeConfiguration.addConfiguration(new SystemConfiguration());
        return compositeConfiguration;
    }

    public static void configureIrc(GameManager gameManager) throws Exception {
        gameManager.getIrcBotService().startAsync();
    }
}
