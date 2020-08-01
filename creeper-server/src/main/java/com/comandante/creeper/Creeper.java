package com.comandante.creeper;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.PickledGraphite;
import com.comandante.creeper.configuration.ConfigureCommands;
import com.comandante.creeper.configuration.ConfigureNpc;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SessionManager;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.player.PlayerManagementManager;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.server.SshInterface;
import com.comandante.creeper.server.player_communication.ChannelUtils;
import com.comandante.creeper.server.telnet.CreeperServer;
import com.comandante.creeper.storage.MapDBCreeperStorage;
import com.comandante.creeper.storage.WorldStorage;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.RoomManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.google.common.util.concurrent.AbstractIdleService;
import events.ListenerService;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Creeper extends AbstractIdleService {

    private final CreeperConfiguration creeperConfiguration;
    private MapDBCreeperStorage mapDBCreeperStorage;
    private PlayerManager playerManager;
    private RoomManager roomManager;
    private PlayerManagementManager playerManagementManager;
    private MapsManager mapsManager;
    private GameManager gameManager;
    private WorldStorage worldStorage;
    private ChannelUtils channelUtils;
    private EntityManager entityManager;
    private CreeperServer creeperServer;

    private static final Logger log = Logger.getLogger(Creeper.class);

    public Creeper(CreeperConfiguration creeperConfiguration) {
        this.creeperConfiguration = creeperConfiguration;
    }

    @Override
    protected void startUp() throws Exception {

        final JmxReporter jmxReporter = JmxReporter.forRegistry(metrics).build();
        jmxReporter.start();

        if (creeperConfiguration.isProduction() && creeperConfiguration.isGraphite()) {
            final PickledGraphite pickledGraphite = new PickledGraphite(new InetSocketAddress(creeperConfiguration.getGraphiteHost(), creeperConfiguration.getGraphitePort()));
            final GraphiteReporter reporter = GraphiteReporter.forRegistry(metrics)
                    .prefixedWith(creeperConfiguration.getDefaultName())
                    .convertRatesTo(TimeUnit.SECONDS)
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .filter(MetricFilter.ALL)
                    .build(pickledGraphite);
            reporter.start(1, TimeUnit.MINUTES);
        }

        SshInterface sshInterface = new SshInterface();
        sshInterface.configureServer();

        Files.isDirectory().apply(new File("world/"));

        DB db = DBMaker.fileDB(new File("world/" + creeperConfiguration.getDatabaseFileName()))
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();

        EventBus eventBus = new AsyncEventBus(Executors.newFixedThreadPool(40));

        ListenerService listenerService = new ListenerService(eventBus);
        listenerService.startAsync().awaitRunning();


        mapDBCreeperStorage = new MapDBCreeperStorage(db);
        mapDBCreeperStorage.startAsync();
        mapDBCreeperStorage.awaitRunning();

        playerManager = new PlayerManager(mapDBCreeperStorage, new SessionManager(), listenerService, registerJdkModuleAndGetMapper());
        playerManager.createAllGauges();

        roomManager = new RoomManager(playerManager);

        startUpMessage("Configuring core systems.");
        mapsManager = new MapsManager(creeperConfiguration, roomManager, listenerService);
        channelUtils = new ChannelUtils(playerManager, roomManager);
        entityManager = new EntityManager(mapDBCreeperStorage, roomManager, playerManager);
        gameManager = new GameManager(mapDBCreeperStorage, creeperConfiguration, roomManager, playerManager, entityManager, mapsManager, channelUtils, HttpClients.createDefault(), listenerService);

        startUpMessage("Reading world from disk.");
        worldStorage = new WorldStorage(roomManager, mapsManager, gameManager.getFloorManager(), entityManager, gameManager);
        worldStorage.readWorldFromDisk();

        startUpMessage("Creating and registering Player Management MBeans.");
        playerManagementManager = new PlayerManagementManager(gameManager);
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


        startUpMessage("Configure quest giver commands");
        ConfigureCommands.configureQuestGiver(gameManager);

        startUpMessage("Configuring npcs and merchants");
        ConfigureNpc.configure(entityManager, gameManager);
        creeperServer = new CreeperServer(creeperConfiguration.getTelnetPort());

        startUpMessage("Generating map data.");
        mapsManager.generateAllMaps();

        startUpMessage("Creeper MUD engine started");

        creeperServer.run(gameManager);
        startUpMessage("Creeper MUD engine online");

        if (creeperConfiguration.isIrcEnabled()) {
            startUpMessage("Starting irc server.");
            configureIrc(gameManager);
        }
    }

    @Override
    protected void shutDown() throws Exception {

    }


    final public static MetricRegistry metrics = new MetricRegistry();

    final public static Set<Character> vowels = new HashSet<Character>(Arrays.asList('a', 'e', 'i', 'o', 'u'));

    public static String getCreeperVersion() {
        Properties props = new Properties();
        try {
            props.load(Creeper.class.getResourceAsStream("/build.properties"));
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

    public static void startUpMessage(String message) {
        log.info(message);
    }

    public static String createPlayerId(String playerName) {
        return new String(Base64.encodeBase64(playerName.getBytes()));
    }

    public static void configureIrc(GameManager gameManager) throws Exception {
        gameManager.getIrcBotService().startAsync();
    }

    public CreeperConfiguration getCreeperConfiguration() {
        return creeperConfiguration;
    }

    public MapDBCreeperStorage getMapDBCreeperStorage() {
        return mapDBCreeperStorage;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public PlayerManagementManager getPlayerManagementManager() {
        return playerManagementManager;
    }

    public MapsManager getMapsManager() {
        return mapsManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public WorldStorage getWorldStorage() {
        return worldStorage;
    }

    public ChannelUtils getChannelUtils() {
        return channelUtils;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public CreeperServer getCreeperServer() {
        return creeperServer;
    }

    public static ObjectMapper registerJdkModuleAndGetMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        Jdk8Module module = new Jdk8Module();
        module.configureAbsentsAsNulls(true);
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
