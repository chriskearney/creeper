package com.comandante.creeper;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.PickledGraphite;
import com.comandante.creeper.configuration.ConfigureCommands;
import com.comandante.creeper.configuration.ConfigureNpc;
import com.comandante.creeper.configuration.CreeperConfiguration;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SessionManager;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.player.PlayerManagementManager;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.server.player_communication.ChannelUtils;
import com.comandante.creeper.server.telnet.CreeperServer;
import com.comandante.creeper.storage.MapDBCreeperStorage;
import com.comandante.creeper.storage.WorldStorage;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.RoomManager;
import com.google.common.io.Files;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.*;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        try {
            long buildTimestamp = Long.parseLong(timestampString);
            Date date = new Date(buildTimestamp);
            SimpleDateFormat format = new SimpleDateFormat();
            String dateFormatted = format.format(date);
            return buildVersion + " " + dateFormatted;
        } catch (NumberFormatException e) {
            return "creeper-local-dev";
        }
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

        DB db = DBMaker.fileDB(new File("world/" + creeperConfiguration.databaseFileName))
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();

        MapDBCreeperStorage mapDBCreeperStorage = new MapDBCreeperStorage(db);
        mapDBCreeperStorage.startAsync();
        mapDBCreeperStorage.awaitRunning();

        PlayerManager playerManager = new PlayerManager(mapDBCreeperStorage, new SessionManager());
        playerManager.createAllGauges();

        RoomManager roomManager = new RoomManager(playerManager);

        startUpMessage("Configuring core systems.");
        MapsManager mapsManager = new MapsManager(creeperConfiguration, roomManager);
        ChannelUtils channelUtils = new ChannelUtils(playerManager, roomManager);
        EntityManager entityManager = new EntityManager(mapDBCreeperStorage, roomManager, playerManager);
        GameManager gameManager = new GameManager(mapDBCreeperStorage, creeperConfiguration, roomManager, playerManager, entityManager, mapsManager, channelUtils, HttpClients.createDefault());

        startUpMessage("Reading world from disk.");
        WorldStorage worldExporter = new WorldStorage(roomManager, mapsManager, gameManager.getFloorManager(), entityManager, gameManager);
        worldExporter.readWorldFromDisk();

        startUpMessage("Creating and registering Player Management MBeans.");
        PlayerManagementManager playerManagementManager = new PlayerManagementManager(gameManager);
        playerManagementManager.processPlayersMarkedForDeletion();
        playerManagementManager.createAndRegisterAllPlayerManagementMBeans();

        startUpMessage("Configuring commands");
        ConfigureCommands.configure(gameManager);

        startUpMessage("Configure Bank commands");
        ConfigureCommands.configureBankCommands(gameManager);

        startUpMessage("Configure Locker commands");
        ConfigureCommands.configureLockerCommands(gameManager);

        startUpMessage("Configure Player Class Selection commands");
        ConfigureCommands.configurePlayerClassSelector(gameManager);

        startUpMessage("Configuring npcs and merchants");
        ConfigureNpc.configure(entityManager, gameManager);
        CreeperServer creeperServer = new CreeperServer(creeperConfiguration.telnetPort);

        startUpMessage("Generating map data.");
        mapsManager.generateAllMaps();

        startUpMessage("Creeper MUD engine started");

        creeperServer.run(gameManager);
        startUpMessage("Creeper MUD engine online");

        if (creeperConfiguration.isIrcEnabled) {
            startUpMessage("Starting irc server.");
            configureIrc(gameManager);
        }
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
