package com.comandante.creeper.core_game;


import com.comandante.creeper.Creeper;
import com.comandante.creeper.bot.IrcBotService;
import com.comandante.creeper.bot.command.BotCommandFactory;
import com.comandante.creeper.bot.command.BotCommandManager;
import com.comandante.creeper.bot.command.commands.BotCommand;
import com.comandante.creeper.chat.Gossip;
import com.comandante.creeper.chat.Users;
import com.comandante.creeper.chat.Utils;
import com.comandante.creeper.common.FriendlyTime;
import com.comandante.creeper.core_game.service.CreeperAsyncJobService;
import com.comandante.creeper.core_game.service.TimeTracker;
import com.comandante.creeper.dropwizard.CreeperConfiguration;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.EffectsManager;
import com.comandante.creeper.items.ForageManager;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.items.ItemDecayManager;
import com.comandante.creeper.items.ItemUseHandler;
import com.comandante.creeper.items.LockPickingManager;
import com.comandante.creeper.items.LootManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcMover;
import com.comandante.creeper.player.CoolDown;
import com.comandante.creeper.player.CoolDownType;
import com.comandante.creeper.player.Player;
import com.comandante.creeper.player.PlayerManager;
import com.comandante.creeper.player.PlayerMetadata;
import com.comandante.creeper.player.PlayerMovement;
import com.comandante.creeper.server.ASCIIArt;
import com.comandante.creeper.server.multiline.MultiLineInputManager;
import com.comandante.creeper.server.player_communication.ChannelCommunicationUtils;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.server.player_communication.GossipCache;
import com.comandante.creeper.spawner.NpcSpawner;
import com.comandante.creeper.spells.Spells;
import com.comandante.creeper.stats.Levels;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.stats.StatsBuilder;
import com.comandante.creeper.stats.modifier.StatsModifierFactory;
import com.comandante.creeper.storage.FilebasedJsonStorage;
import com.comandante.creeper.storage.ItemStorage;
import com.comandante.creeper.storage.MapDBCreeperStorage;
import com.comandante.creeper.storage.MerchantStorage;
import com.comandante.creeper.storage.NpcStorage;
import com.comandante.creeper.world.FloorManager;
import com.comandante.creeper.world.MapsManager;
import com.comandante.creeper.world.RoomManager;
import com.comandante.creeper.world.model.BasicRoomBuilder;
import com.comandante.creeper.world.model.Coords;
import com.comandante.creeper.world.model.RemoteExit;
import com.comandante.creeper.world.model.Room;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.Lists;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import events.ListenerService;
import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import static com.comandante.creeper.server.player_communication.Color.BOLD_OFF;
import static com.comandante.creeper.server.player_communication.Color.BOLD_ON;
import static com.comandante.creeper.server.player_communication.Color.RESET;

public class GameManager {

    public static final Integer LOBBY_ID = 1;
    private static final Logger log = Logger.getLogger(GameManager.class);
    public static String LOGO = "Creeper.";
    private final RoomManager roomManager;
    private final PlayerManager playerManager;
    private final ChannelCommunicationUtils channelUtils;
    private final NewUserRegistrationManager newUserRegistrationManager;
    private final EntityManager entityManager;
    private final ItemDecayManager itemDecayManager;
    private final MultiLineInputManager multiLineInputManager;
    private final MapsManager mapsManager;
    private final FloorManager floorManager;
    private final LootManager lootManager;
    private final IrcBotService ircBotService;
    private final CreeperConfiguration creeperConfiguration;
    private final ForageManager forageManager;
    private final EffectsManager effectsManager;
    private final BotCommandFactory botCommandFactory;
    private final BotCommandManager botCommandManager;
    private final StatsModifierFactory statsModifierFactory;
    private final GossipCache gossipCache;
    private final Interner<String> interner = Interners.newWeakInterner();
    private final TimeTracker timeTracker;
    private final ItemUseHandler itemUseHandler;
    private final NpcMover npcMover;
    private final Spells spells;
    private final CreeperAsyncJobService eventProcessor = new CreeperAsyncJobService(new ArrayBlockingQueue<>(10000));
    private final Room detainmentRoom;
    private final NpcStorage npcStorage;
    private final ItemStorage itemStorage;
    private final HttpClient httpclient;
    private final Gson gson;
    private final FilebasedJsonStorage filebasedJsonStorage;
    private final MapDBCreeperStorage mapDBCreeperStorage;
    private final ListenerService listenerService;
    private final ObjectMapper objectMapper = Creeper.registerJdkModuleAndGetMapper();
    private final MerchantStorage merchantStorage;
    private final LockPickingManager lockPickingManager;


    public GameManager(MapDBCreeperStorage mapDBCreeperStorage,
                       CreeperConfiguration creeperConfiguration,
                       RoomManager roomManager,
                       PlayerManager playerManager,
                       EntityManager entityManager,
                       MapsManager mapsManager,
                       ChannelCommunicationUtils channelUtils,
                       HttpClient httpClient,
                       ListenerService listenerService) {
        this.mapDBCreeperStorage = mapDBCreeperStorage;
        this.roomManager = roomManager;
        this.playerManager = playerManager;
        this.entityManager = entityManager;
        this.newUserRegistrationManager = new NewUserRegistrationManager(this);
        this.multiLineInputManager = new MultiLineInputManager();
        this.mapsManager = mapsManager;
        this.floorManager = new FloorManager();
        this.channelUtils = channelUtils;
        this.lootManager = new LootManager(this);
        this.ircBotService = new IrcBotService(creeperConfiguration, this);
        this.creeperConfiguration = creeperConfiguration;
        this.forageManager = new ForageManager(this);
        this.effectsManager = new EffectsManager(this);
        this.botCommandManager = new BotCommandManager(this);
        this.botCommandFactory = new BotCommandFactory(botCommandManager);
        this.statsModifierFactory = new StatsModifierFactory(this);
        this.gossipCache = new GossipCache(this);
        this.timeTracker = new TimeTracker(this);
        this.entityManager.addEntity(timeTracker);
        this.itemDecayManager = new ItemDecayManager(entityManager, this);
        this.entityManager.addEntity(itemDecayManager);
        this.itemUseHandler = new ItemUseHandler(this);
        this.npcMover = new NpcMover(this);
        this.spells = new Spells(this);
        this.eventProcessor.startAsync();
        this.detainmentRoom = buildDetainmentRoom();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.filebasedJsonStorage = new FilebasedJsonStorage(gson);
        this.npcStorage = new NpcStorage(this, filebasedJsonStorage);
        this.itemStorage = new ItemStorage(filebasedJsonStorage);
        this.merchantStorage = new MerchantStorage(this, filebasedJsonStorage);
        this.httpclient = httpClient;
        this.listenerService = listenerService;
        this.lockPickingManager = new LockPickingManager(this);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public MerchantStorage getMerchantStorage() {
        return merchantStorage;
    }


    public MapDBCreeperStorage getMapDBCreeperStorage() {
        return mapDBCreeperStorage;
    }

    public Gson getGson() {
        return gson;
    }

    public ItemStorage getItemStorage() {
        return itemStorage;
    }

    private Room buildDetainmentRoom() {
        BasicRoomBuilder basicRoomBuilder = new BasicRoomBuilder(this);

        basicRoomBuilder.setRoomDescription("The room is covered in a white soft padded material.");
        basicRoomBuilder.setRoomTitle("Detainment");
        Room detainmentRoom = basicRoomBuilder
                .setRoomId(-187)
                .setFloorId(-187)
                .createBasicRoom();
        roomManager.addRoom(detainmentRoom);
        return detainmentRoom;
    }

    public Spells getSpells() {
        return spells;
    }

    public NpcMover getNpcMover() {
        return npcMover;
    }

    public ItemUseHandler getItemUseHandler() {
        return itemUseHandler;
    }

    public GossipCache getGossipCache() {
        return gossipCache;
    }

    public StatsModifierFactory getStatsModifierFactory() {
        return statsModifierFactory;
    }

    public BotCommandFactory getBotCommandFactory() {
        return botCommandFactory;
    }

    public BotCommandManager getBotCommandManager() {
        return botCommandManager;
    }

    public EffectsManager getEffectsManager() {
        return effectsManager;
    }

    public ForageManager getForageManager() {
        return forageManager;
    }

    public IrcBotService getIrcBotService() {
        return ircBotService;
    }

    public CreeperConfiguration getCreeperConfiguration() {
        return creeperConfiguration;
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public FloorManager getFloorManager() {
        return floorManager;
    }

    public MapsManager getMapsManager() {
        return mapsManager;
    }

    public MultiLineInputManager getMultiLineInputManager() {
        return multiLineInputManager;
    }

    public NewUserRegistrationManager getNewUserRegistrationManager() {
        return newUserRegistrationManager;
    }

    public ItemDecayManager getItemDecayManager() {
        return itemDecayManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public TimeTracker getTimeTracker() {
        return timeTracker;
    }

    public CreeperAsyncJobService getEventProcessor() {
        return eventProcessor;
    }

    public NpcStorage getNpcStorage() {
        return npcStorage;
    }

    public ListenerService getListenerService() {
        return listenerService;
    }

    public LockPickingManager getLockPickingManager() {
        return lockPickingManager;
    }

    private String getBotCommandOutput(String cmd) {
        ArrayList<String> originalMessageParts = com.google.common.collect.Lists.newArrayList(Arrays.asList(cmd.split("!!")));
        originalMessageParts.remove(0);
        final String msg = Joiner.on(" ").join(originalMessageParts);
        BotCommand command = getBotCommandFactory().getCommand(null, msg);
        if (command != null) {
            List<String> process = command.process();
            StringBuilder sb = new StringBuilder();
            for (String line : process) {
                sb.append(line).append("\r\n");
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public void gossip(Player player, String message) {
        gossip(player, message, false);
    }

    public void gossip(Player player, String message, boolean apiSource) {
        try {
            if (message.startsWith("!!")) {
                String botCommandOutput = getBotCommandOutput(message);
                message = message + "\r\n" + botCommandOutput;
            }
        } catch (Exception ex) {
            log.error("Problem executing bot command from gossip channel!", ex);
        }
        String gossipMessage = Utils.buildGossipString("", player.getPlayerName(), message);
        playerManager.getAllPlayersMap().forEach((s, destinationPlayer) -> {
            if (destinationPlayer.getPlayerId().equals(player.getPlayerId())) {
                if (apiSource) {
                    channelUtils.write(player.getPlayerId(), gossipMessage + "\r\n", true);

                } else {
                    channelUtils.write(player.getPlayerId(), gossipMessage);
                }
            } else {
                channelUtils.write(destinationPlayer.getPlayerId(), gossipMessage + "\r\n", true);
            }
        });

        try {
            CreeperEvent build = new CreeperEvent.Builder()
                    .playerId(player.getPlayerId())
                    .payload(objectMapper.writeValueAsString(new Gossip(message, player.getPlayerName(), "", System.currentTimeMillis())))
                    .epochTimestamp(System.currentTimeMillis())
                    .creeperEventType(CreeperEventType.GOSSIP)
                    .audience(CreeperEvent.Audience.EVERYONE)
                    .build();

            getListenerService().post(build);
        } catch (Exception e) {
            log.error("Problem serializing creeper event.", e);
        }
        getGossipCache().addGossipLine(gossipMessage);

    }

    public void placePlayerInLobby(Player player) {
        Room room = roomManager.getRoom(LOBBY_ID);
        room.addPresentPlayer(player.getPlayerId());
        player.setCurrentRoomAndPersist(room);
        for (Player next : room.getPresentPlayers()) {
            if (next.getPlayerId().equals(player.getPlayerId())) {
                continue;
            }
            channelUtils.write(next.getPlayerId(), player.getPlayerName() + " arrived.", true);
        }
    }

    public void detainPlayer(Player player) {
        // Source room Id is null, meaning it will disable things like the "Back" command.  Since its null you need to
        // remove the player from the current room manually.
        PlayerMovement playerMovement = new PlayerMovement(player,
                null,
                detainmentRoom.getRoomId(),
                "has been placed under arrest.");

        player.removePlayerFromRoom(player.getCurrentRoom());
        player.movePlayer(playerMovement);
        playerManager.getAllPlayersMap().forEach((s, destinationPlayer) -> {
            channelUtils.write(destinationPlayer.getPlayerId(),
                    player.getPlayerName() + " has been " + Color.BOLD_ON + Color.RED + "DETAINED" + Color.RESET + "!" + "\r\n", true);
        });
        player.addCoolDown(new CoolDown(CoolDownType.DETAINMENT));
    }

    public void announceConnect(String userName) {
        Set<Player> allPlayers = getAllPlayers();
        for (Player p : allPlayers) {
            getChannelUtils().write(p.getPlayerId(), Color.GREEN + userName + " has connected." + Color.RESET + "\r\n", true);
        }
    }

    public Set<Player> getAllPlayers() {
        ImmutableSet.Builder<Player> builder = ImmutableSet.builder();
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRoomsIterator();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Room room = next.getValue();
            Set<Player> presentPlayers = room.getPresentPlayers();
            for (Player player : presentPlayers) {
                builder.add(player);
            }
        }
        return builder.build();
    }

    public ChannelCommunicationUtils getChannelUtils() {
        return channelUtils;
    }

    public void currentRoomLogic(String playerId) {
        Player player = playerManager.getPlayer(playerId);
        final Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        currentRoomLogic(playerId, playerCurrentRoom);
    }

    public void fireDrawMapEvent(String playerId, Room room) {
        String map = getMapsManager().drawMap(room.getRoomId(), new Coords(10, 10));
        CreeperEvent build = new CreeperEvent.Builder()
                .playerId(playerId)
                .payload(map)
                .epochTimestamp(System.currentTimeMillis())
                .creeperEventType(CreeperEventType.DRAW_MAP)
                .audience(CreeperEvent.Audience.PLAYER_ONLY)
                .build();

        getListenerService().post(build);
    }


    public void currentRoomLogic(String playerId, Room playerCurrentRoom) {
        Player player = playerManager.getPlayer(playerId);
        StringBuilder sb = new StringBuilder();
        sb.append(Color.BOLD_ON + Color.GREEN);
        sb.append(playerCurrentRoom.getRoomTitle());
        sb.append(RESET);
        sb.append("\r\n\r\n");
        sb.append(ASCIIArt.wrap(playerCurrentRoom.getRoomDescription())).append("\r\n").append("\r\n");
        Optional<String> autoMapOptional = player.getPlayerSetting("auto_map");
        if (playerCurrentRoom.getMapData().isPresent()) {
            fireDrawMapEvent(playerId, playerCurrentRoom);
        }
        if (playerCurrentRoom.getMapData().isPresent() && autoMapOptional.isPresent()) {
            int i = Integer.parseInt(autoMapOptional.get());
            sb.append(mapsManager.drawMap(playerCurrentRoom.getRoomId(), new Coords(i, i))).append("\r\n");
        }
        sb.append(getExits(playerCurrentRoom, player)).append("\r\n");

        Set<Merchant> merchants = playerCurrentRoom.getMerchants();
        for (Merchant merchant : merchants) {
            sb.append(merchant.getColorName()).append(" is here.").append(RESET).append("\r\n");
        }
        for (Player searchPlayer : playerCurrentRoom.getPresentPlayers()) {
            if (searchPlayer.getPlayerId().equals(player.getPlayerId())) {
                continue;
            }
            sb.append(searchPlayer.getPlayerName()).append(" is here.").append(RESET).append("\r\n");
        }

        for (String itemId : playerCurrentRoom.getItemIds()) {
            Optional<Item> itemOptional = entityManager.getItemEntity(itemId);
            if (!itemOptional.isPresent()) {
                playerCurrentRoom.removePresentItem(itemId);
                continue;
            }
            Item item = itemOptional.get();
            sb.append("   ").append(item.getRestingName()).append("\r\n");
        }

        List<String> npcs = Lists.newArrayList();
        for (String npcId : playerCurrentRoom.getNpcIds()) {
            StringBuilder sbb = new StringBuilder();
            Npc npcEntity = entityManager.getNpcEntity(npcId);
            if (Creeper.vowels.contains(Character.toLowerCase(npcEntity.getName().charAt(0)))) {
                sbb.append("an ");
            } else {
                sbb.append("a ");
            }
            sbb.append(npcEntity.getColorName()).append(" is here.\r\n");
            npcs.add(sbb.toString());
        }
        Collections.sort(npcs, String.CASE_INSENSITIVE_ORDER);
        for (String s : npcs) {
            sb.append(s);
        }
        String msg = null;
        if (sb.toString().substring(sb.toString().length() - 2).equals("\r\n")) {
            CharSequence charSequence = sb.toString().subSequence(0, sb.toString().length() - 2);
            msg = charSequence.toString();
        } else {
            msg = sb.toString();
        }
        channelUtils.write(player.getPlayerId(), msg);
    }

    private String getExits(Room room, Player player) {
        int numExits = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        sb.append(BOLD_ON);
        sb.append(Color.GREEN);
        if (!player.getReturnDirection().isPresent()) {
            player.setReturnDirection(Optional.of("-"));
        }
        if (room.getNorthId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("north")) {
                sb.append(BOLD_OFF);
                sb.append("North ");
                sb.append(BOLD_ON);
            } else {
                sb.append("North ");
            }
            numExits++;
        }
        if (room.getSouthId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("south")) {
                sb.append(BOLD_OFF);
                sb.append("South ");
                sb.append(BOLD_ON);
            } else {
                sb.append("South ");
            }
            numExits++;
        }
        if (room.getEastId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("east")) {
                sb.append(BOLD_OFF);
                sb.append("East ");
                sb.append(BOLD_ON);
            } else {
                sb.append("East ");
            }
            numExits++;
        }
        if (room.getWestId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("west")) {
                sb.append(BOLD_OFF);
                sb.append("West ");
                sb.append(BOLD_ON);
            } else {
                sb.append("West ");
            }
            numExits++;
        }
        if (room.getUpId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("up")) {
                sb.append(BOLD_OFF);
                sb.append("Up ");
                sb.append(BOLD_ON);
            } else {
                sb.append("Up ");
            }
            numExits++;
        }
        if (room.getDownId().isPresent()) {
            if (player.getReturnDirection().get().equalsIgnoreCase("down")) {
                sb.append(BOLD_OFF);
                sb.append("Down ");
                sb.append(BOLD_ON);
            } else {
                sb.append("Down ");
            }
            numExits++;
        }
        if (room.getEnterExits() != null && room.getEnterExits().size() > 0) {
            List<RemoteExit> enters = room.getEnterExits();
            for (RemoteExit enter : enters) {
                sb.append("e-" + enter.getExitDetail() + " ");
                numExits++;
            }
        }
        String fin = null;
        if (numExits == 1) {
            fin = sb.toString().replace(BOLD_OFF, BOLD_ON);
        } else {
            fin = sb.toString();
        }
        fin = fin + RESET + "]\r\n";
        return fin;
    }

    public void placeItemInRoom(Integer roomId, String itemId) {
        Optional<Item> itemOptional = entityManager.getItemEntity(itemId);
        if (!itemOptional.isPresent()) {
            return;
        }
        Item item = itemOptional.get();
        roomManager.getRoom(roomId).addPresentItem(item.getItemId());
    }

    public boolean acquireItemFromRoom(Player player, String itemId) {
        synchronized (interner.intern(itemId)) {
            Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
            if (playerCurrentRoom.getItemIds().contains(itemId)) {
                if (acquireItem(player, itemId)) {
                    playerCurrentRoom.getItemIds().remove(itemId);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean acquireItem(Player player, String itemId) {
        return acquireItem(player, itemId, false);
    }

    public boolean acquireItem(Player player, String itemId, boolean isFromLoot) {
        return acquireItem(Optional.empty(), player, itemId, isFromLoot);
    }

    public boolean acquireItem(Optional<PlayerMetadata> playerMetadataOptional, Player player, String itemId, boolean isFromLoot) {
        synchronized (interner.intern(itemId)) {
            Stats playerStatsWithEquipmentAndLevel = player.getPlayerStatsWithEquipmentAndLevel();
            if (player.getInventory().size() < playerStatsWithEquipmentAndLevel.getInventorySize()) {
                Optional<Item> itemOptional = entityManager.getItemEntity(itemId);
                if (!itemOptional.isPresent()) {
                    return false;
                }
                Item itemEntity = itemOptional.get();
                itemEntity.setWithPlayer(true);
                player.addInventoryId(playerMetadataOptional, itemId);
                entityManager.saveItem(itemEntity);
                return true;
            } else {
                Optional<Item> itemOptional = entityManager.getItemEntity(itemId);
                if (!itemOptional.isPresent()) {
                    return false;
                }
                Item itemEntity = itemOptional.get();
                channelUtils.write(player.getPlayerId(), "Your inventory is full, drop some items to free up room.\r\n");
                if (isFromLoot) {
                    player.getCurrentRoom().addPresentItem(itemId);
                    roomSay(player.getCurrentRoom().getRoomId(), player.getPlayerName() + " dropped " + itemEntity.getItemName(), player.getPlayerId() + "\r\n");
                }
                return false;
            }
        }
    }

    public void roomSay(Integer roomId, String message, String sourcePlayerId) {
        this.roomSay(roomId, message, sourcePlayerId, false);
    }

    public void roomSay(Integer roomId, String message, String sourcePlayerId, boolean fromApi) {
        Set<Player> presentPlayers = roomManager.getRoom(roomId).getPresentPlayers();
        for (Player player : presentPlayers) {
            if (player.getPlayerId().equals(sourcePlayerId)) {
                channelUtils.write(player.getPlayerId(), message, fromApi);
                continue;
            }
            channelUtils.write(player.getPlayerId(), message, true);
        }
    }

    public String getLookString(Npc npc, long playerLevel) {
        StringBuilder sb = new StringBuilder();
        // passing an empty createState because of the "difference calculation"
        sb.append(Color.MAGENTA + "-+=[ " + Color.RESET).append(npc.getColorName()).append(Color.MAGENTA + " ]=+- " + Color.RESET).append("\r\n");
        sb.append("Level ").append(Levels.getLevel(npc.getStats().getExperience())).append(" ")
                .append(npc.getLevelColor((int) playerLevel).getColor())
                .append(" [").append(npc.getTemperament().getFriendlyFormat()).append("]").append("\r\n");
        sb.append(Color.MAGENTA + "Stats--------------------------------" + Color.RESET).append("\r\n");
        sb.append(buildLookString(npc.getColorName(), npc.getStats(), new StatsBuilder().createStats())).append("\r\n");
        if (npc.getEffects() != null && npc.getEffects().size() > 0) {
            sb.append(Color.MAGENTA + "Effects--------------------------------" + Color.RESET).append("\r\n");
            sb.append(buldEffectsString(npc)).append("\r\n");
        }
        return sb.toString();
    }

    public String buildLookString(String name, Stats stats, Stats diff) {
        StringBuilder returnString = new StringBuilder();
        Table t = new Table(3, BorderStyle.CLASSIC_COMPATIBLE,
                ShownBorders.NONE);

        t.setColumnWidth(0, 16, 20);
        t.setColumnWidth(1, 10, 20);


        t.addCell("Experience");
        t.addCell(NumberFormat.getNumberInstance(Locale.US).format(stats.getExperience()));
        t.addCell("");

        t.addCell("Health");
        t.addCell(getFormattedNumber(stats.getCurrentHealth()));
        t.addCell("");

        t.addCell("Mana");
        t.addCell(getFormattedNumber(stats.getCurrentMana()));
        t.addCell("");

        StringBuilder sb = new StringBuilder();
        t.addCell("Strength");
        t.addCell(getFormattedNumber(stats.getStrength()));
        if (diff.getStrength() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getStrength())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Intelligence");
        t.addCell(getFormattedNumber(stats.getIntelligence()));
        if (diff.getStrength() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getIntelligence())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Willpower");
        t.addCell(getFormattedNumber(stats.getWillpower()));
        if (diff.getWillpower() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getWillpower())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Aim");
        t.addCell(getFormattedNumber(stats.getAim()));
        if (diff.getAim() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getAim())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Agile");
        t.addCell(getFormattedNumber(stats.getAgile()));
        if (diff.getAgile() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getAgile())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Armor");
        t.addCell(getFormattedNumber(stats.getArmorRating()));
        if (diff.getArmorRating() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getArmorRating())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Mele");
        t.addCell(getFormattedNumber(stats.getMeleSkill()));
        if (diff.getMeleSkill() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getMeleSkill())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Weapon Rating");
        t.addCell(getFormattedNumber(stats.getWeaponRatingMin()) + "-" + getFormattedNumber(stats.getWeaponRatingMax()));
        if (diff.getWeaponRatingMin() > 0 || diff.getWeaponRatingMax() > 0) {
            sb.append("(");
            if (diff.getWeaponRatingMin() > 0) {
                sb.append(Color.GREEN);
                sb.append("+");
            }
            sb.append(Long.toString(diff.getWeaponRatingMin())).append(Color.RESET).append("-");
            if (diff.getWeaponRatingMax() > 0) {
                sb.append(Color.GREEN);
                sb.append("+");
            }
            sb.append(getFormattedNumber(diff.getWeaponRatingMax()));
            sb.append(Color.RESET).append(")");
        }
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Forage");
        t.addCell(getFormattedNumber(stats.getForaging()));
        if (diff.getForaging() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getForaging())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        sb = new StringBuilder();
        t.addCell("Bag");
        t.addCell(getFormattedNumber(stats.getInventorySize()));
        if (diff.getInventorySize() > 0)
            sb.append("(").append(Color.GREEN).append("+").append(getFormattedNumber(diff.getInventorySize())).append(Color.RESET).append(")");
        t.addCell(sb.toString());

        returnString.append(t.render());
        return returnString.toString();
    }

    public String buldEffectsString(Npc npc) {
        return renderEffectsString(npc.getEffects());

    }

    private String getFormattedNumber(Long longval) {
        return NumberFormat.getNumberInstance(Locale.US).format(longval);
    }

    public String renderEffectsString(List<Effect> effects) {
        Table t = new Table(2, BorderStyle.CLASSIC_COMPATIBLE,
                ShownBorders.NONE);

        t.setColumnWidth(0, 16, 20);
        // t.setColumnWidth(1, 10, 13);

        int i = 1;
        for (Effect effect : effects) {
            int percent = 100 - (int) ((effect.getEffectApplications() * 100.0f) / effect.getMaxEffectApplications());
            t.addCell(drawProgressBar(percent));
            t.addCell(effect.getEffectName());
            i++;
        }
        return t.render();
    }

    public String drawProgressBar(int pct) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int numberOfProgressBarNotches = getNumberOfProgressBarNotches(pct);
        for (int i = 0; i < numberOfProgressBarNotches; i++) {
            sb.append("+");
        }
        for (int i = numberOfProgressBarNotches; i < 10; i++) {
            sb.append(" ");
        }
        sb.append("]");
        return sb.toString();
    }

    public int getNumberOfProgressBarNotches(int y) {
        int x = (int) (Math.round(y / 10.0) * 10);
        String str = Integer.toString(x);
        if (str.length() > 1) {
            str = str.substring(0, str.length() - 1);
        }
        return Integer.parseInt(str);
    }

    public String renderCoolDownString(Set<CoolDown> coolDowns) {
        Table t = new Table(2, BorderStyle.CLASSIC_COMPATIBLE,
                ShownBorders.NONE);

        t.setColumnWidth(0, 19, 25);
        // t.setColumnWidth(1, 10, 13);

        int i = 1;
        for (CoolDown coolDown : coolDowns) {
            int percent = 100 - (int) (((coolDown.getOriginalNumberOfTicks() - coolDown.getNumberOfTicks()) * 100.0f) / coolDown.getOriginalNumberOfTicks());
            // 1 tick == .5 seconds.
            int approxSecondsRemaining = coolDown.getNumberOfTicks() / 2;
            FriendlyTime friendlyTime = new FriendlyTime(approxSecondsRemaining);
            String friendlyFormattedShort = friendlyTime.getFriendlyFormattedShort();
            t.addCell(drawProgressBar(percent) + friendlyFormattedShort);
            t.addCell(coolDown.getName());
            i++;
        }
        return t.render();
    }

    public void writeToPlayerCurrentRoom(String playerId, String message) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        Player player = playerManager.getPlayer(playerId);
        Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        Set<Player> presentPlayers = playerCurrentRoom.getPresentPlayers();
        for (Player presentPlayer : presentPlayers) {
            channelUtils.write(presentPlayer.getPlayerId(), message, true);
        }
    }

    public void writeToRoom(Integer roomId, String message) {
        Room room = roomManager.getRoom(roomId);
        Set<Player> presentPlayers = room.getPresentPlayers();
        for (Player presentPlayer : presentPlayers) {
            channelUtils.write(presentPlayer.getPlayerId(), message, true);
        }
    }

    public void announceLevelUp(String playerName, long previousLevel, long newLevel) {
        Iterator<Map.Entry<String, Player>> players = playerManager.getPlayers();
        while (players.hasNext()) {
            Map.Entry<String, Player> next = players.next();
            channelUtils.write(next.getValue().getPlayerId(), "\r\n" + playerName + Color.BOLD_ON + Color.GREEN + " has reached LEVEL " + newLevel + Color.RESET + "\r\n");
        }
    }

    public Map<String, Double> processExperience(Npc npc, Room npcCurrentRoom) {
        Iterator<Map.Entry<String, Long>> iterator = npc.getPlayerDamageMap().entrySet().iterator();
        int totalDamageDone = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Long> damageEntry = iterator.next();
            totalDamageDone += damageEntry.getValue();
            String playerId = damageEntry.getKey();
            Optional<Room> playerCurrentRoom = getRoomManager().getPlayerCurrentRoom(playerId);
            if (!playerCurrentRoom.isPresent()) {
                iterator.remove();
            } else if (!Objects.equals(npcCurrentRoom.getRoomId(), playerCurrentRoom.get().getRoomId())) {
                iterator.remove();
            }
        }
        Map<String, Double> damagePcts = Maps.newHashMap();
        Set<Map.Entry<String, Long>> entries = npc.getPlayerDamageMap().entrySet();
        for (Map.Entry<String, Long> damageEntry : entries) {
            String playerId = damageEntry.getKey();
            long amount = damageEntry.getValue();
            double pct = (double) amount / totalDamageDone;
            if (pct >= .90) {
                damagePcts.put(playerId, (double) 1);
            } else if (pct >= 0.25) {
                damagePcts.put(playerId, .8);
            } else if (pct >= 0.10) {
                damagePcts.put(playerId, .5);
            } else {
                damagePcts.put(playerId, .25);
            }
        }
        return damagePcts;
    }

    public HttpClient getHttpclient() {
        return httpclient;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public synchronized void removeAllNpcs() {
        for (Npc npc : entityManager.getNpcs().values()) {
            Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRoomsIterator();
            while (rooms.hasNext()) {
                Map.Entry<Integer, Room> next = rooms.next();
                next.getValue().removePresentNpc(npc.getEntityId());
            }
            entityManager.getNpcs().remove(npc.getEntityId());
            entityManager.getEntities().remove(npc.getEntityId());
        }
        for (CreeperEntity creeperEntity : entityManager.getNpcs().values()) {
            if (creeperEntity instanceof NpcSpawner) {
                entityManager.getNpcs().remove(creeperEntity.getEntityId());
            }
        }
    }

    public String buildPrompt(String playerId) {
        Player player = playerManager.getPlayer(playerId);
        boolean isFight = player.isActiveFights();
        Stats stats = player.getPlayerStatsWithEquipmentAndLevel();
        long currentHealth = stats.getCurrentHealth();
        long maxHealth = stats.getMaxHealth();
        long currentMana = stats.getCurrentMana();
        long maxMana = stats.getMaxMana();
        StringBuilder sb = new StringBuilder()
                .append(Color.BOLD_ON + Color.WHITE)
                .append("[")
                .append(Color.RESET)
                .append(player.getPlayerName())
                .append(" ")
                .append(currentHealth)
                .append(Color.BOLD_ON)
                .append(Color.WHITE)
                .append("/")
                .append(Color.RESET)
                .append(maxHealth)
                .append("h")
                .append(" ")
                .append(currentMana)
                .append(Color.BOLD_ON)
                .append(Color.WHITE)
                .append("/")
                .append(Color.RESET)
                .append(maxMana).append("m");
        if (isFight) {
            sb.append(Color.RED + " ! " + Color.RESET);
        }
        if (player.isActiveCoolDown()) {
            if (player.isActive(CoolDownType.DEATH)) {
                sb.append(" ");
                sb.append(Color.RED + "D" + Color.RESET);
            }
            if (player.isActiveForageCoolDown()) {
                sb.append(" ");
                sb.append(Color.YELLOW + "F" + Color.RESET);
            }
            if (player.isActive(CoolDownType.DETAINMENT)) {
                sb.append(" ");
                sb.append(Color.BOLD_ON + Color.RED + "DETAINED" + Color.RESET);
            }
            if (player.isActive(CoolDownType.FIRE_SAUCE)) {
                sb.append(" ");
                sb.append(Color.BOLD_ON + Color.GREEN + "XP+" + Color.RESET);
            }
        }
        if (player.areAnyAlertedNpcsInCurrentRoom()) {
            sb.append(" ");
            sb.append(Color.RED + "ALERT" + Color.RESET);
        }
        sb.append(Color.BOLD_ON + Color.WHITE);
        sb.append("] ");
        sb.append(Color.RESET);
        if (player.isChatModeOn()) {
            sb.append("<" + Color.GREEN + "=" + Color.RESET + "> ");
        }

        return sb.toString();
    }

    public void emitUsersEvent(String playerId) {
        try {
            Map<String, String> users = Maps.newHashMap();
            for (Player p : getAllPlayers()) {
                users.put(p.getPlayerId(), p.getPlayerName());
            }
            CreeperEvent event = new CreeperEvent.Builder()
                    .playerId(playerId)
                    .payload(objectMapper.writeValueAsString(new Users(users, playerId)))
                    .epochTimestamp(System.currentTimeMillis())
                    .creeperEventType(CreeperEventType.USERS)
                    .audience(CreeperEvent.Audience.EVERYONE)
                    .build();

            getListenerService().post(event);
        } catch (Exception e) {
            log.error("Unable to emit event!", e);
        }
    }

    public Room getDetainmentRoom() {
        return detainmentRoom;
    }
}

