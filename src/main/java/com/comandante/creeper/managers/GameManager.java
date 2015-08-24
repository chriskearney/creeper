package com.comandante.creeper.managers;


import com.comandante.creeper.CreeperConfiguration;
import com.comandante.creeper.IrcBotService;
import com.comandante.creeper.Items.*;
import com.comandante.creeper.Main;
import com.comandante.creeper.bot.BotCommandFactory;
import com.comandante.creeper.bot.BotCommandManager;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcMover;
import com.comandante.creeper.player.*;
import com.comandante.creeper.server.ChannelUtils;
import com.comandante.creeper.server.Color;
import com.comandante.creeper.server.GossipCache;
import com.comandante.creeper.server.MultiLineInputManager;
import com.comandante.creeper.spawner.NpcSpawner;
import com.comandante.creeper.spells.Effect;
import com.comandante.creeper.spells.EffectsManager;
import com.comandante.creeper.stat.Stats;
import com.comandante.creeper.stat.StatsBuilder;
import com.comandante.creeper.world.*;
import com.google.api.client.util.Lists;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.text.NumberFormat;
import java.util.*;

import static com.comandante.creeper.server.Color.*;

public class GameManager {

    public static String LOGO = "Creeper.";

    private final RoomManager roomManager;
    private final PlayerManager playerManager;
    private final ChannelUtils channelUtils;
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
    private static final Logger log = Logger.getLogger(GameManager.class);
    private final TimeTracker timeTracker;
    private final ItemUseHandler itemUseHandler;
    private final NpcMover npcMover;
    private final PlayerManagementManager playerManagementManager;

    public GameManager(CreeperConfiguration creeperConfiguration, RoomManager roomManager, PlayerManager playerManager, EntityManager entityManager, MapsManager mapsManager, ChannelUtils channelUtils) {
        this.roomManager = roomManager;
        this.playerManager = playerManager;
        this.entityManager = entityManager;
        this.newUserRegistrationManager = new NewUserRegistrationManager(playerManager);
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
        this.playerManagementManager = new PlayerManagementManager(this);
    }

    public PlayerManagementManager getPlayerManagementManager() {
        return playerManagementManager;
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

    public ChannelUtils getChannelUtils() {
        return channelUtils;
    }

    public ItemDecayManager getItemDecayManager() {
        return itemDecayManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public TimeTracker getTimeTracker() {
        return timeTracker;
    }

    public static final Integer LOBBY_ID = 1;

    public Set<Player> getAllPlayers() {
        ImmutableSet.Builder<Player> builder = ImmutableSet.builder();
        Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
        while (rooms.hasNext()) {
            Map.Entry<Integer, Room> next = rooms.next();
            Room room = next.getValue();
            Set<Player> presentPlayers = roomManager.getPresentPlayers(room);
            for (Player player : presentPlayers) {
                builder.add(player);
            }
        }
        return builder.build();
    }

    public void placePlayerInLobby(Player player) {
        Room room = roomManager.getRoom(LOBBY_ID);
        room.addPresentPlayer(player.getPlayerId());
        player.setCurrentRoom(room);
        for (Player next : roomManager.getPresentPlayers(room)) {
            if (next.getPlayerId().equals(player.getPlayerId())) {
                continue;
            }
            channelUtils.write(next.getPlayerId(), player.getPlayerName() + " arrived.", true);
        }
    }

    public void announceConnect(String userName) {
        Set<Player> allPlayers = getAllPlayers();
        for (Player p : allPlayers) {
            getChannelUtils().write(p.getPlayerId(), Color.GREEN + userName + " has connected." + Color.RESET + "\r\n", true);
        }
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

    public void currentRoomLogic(String playerId) {
        Player player = playerManager.getPlayer(playerId);
        final Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        currentRoomLogic(playerId, playerCurrentRoom);
    }

    public void currentRoomLogic(String playerId, Room playerCurrentRoom) {
        Player player = playerManager.getPlayer(playerId);
        StringBuilder sb = new StringBuilder();
        sb.append(Color.BOLD_ON + Color.GREEN);
        sb.append(playerCurrentRoom.getRoomTitle());
        sb.append(RESET);
        sb.append("\r\n\r\n");
        sb.append(WordUtils.wrap(playerCurrentRoom.getRoomDescription(), 80, "\r\n", true)).append("\r\n").append("\r\n");
        String auto_map = player.getPlayerSetting("auto_map");
        if (playerCurrentRoom.getMapData().isPresent() && auto_map != null) {
            int i = Integer.parseInt(auto_map);
            sb.append(mapsManager.drawMap(playerCurrentRoom.getRoomId(), new Coords(i, i))).append("\r\n");
        }
        sb.append(getExits(playerCurrentRoom, player)).append("\r\n");

        Set<Merchant> merchants = playerCurrentRoom.getMerchants();
        for (Merchant merchant : merchants) {
            sb.append(merchant.getColorName()).append(" is here.").append(RESET).append("\r\n");
        }
        for (Player searchPlayer : roomManager.getPresentPlayers(playerCurrentRoom)) {
            if (searchPlayer.getPlayerId().equals(player.getPlayerId())) {
                continue;
            }
            sb.append(searchPlayer.getPlayerName()).append(" is here.").append(RESET).append("\r\n");
        }

        for (String itemId : playerCurrentRoom.getItemIds()) {
            Item itemEntity = entityManager.getItemEntity(itemId);
            if (itemEntity == null) {
                playerCurrentRoom.removePresentItem(itemId);
                continue;
            }
            sb.append("   ").append(entityManager.getItemEntity(itemId).getRestingName()).append("\r\n");
        }

        List<String> npcs = Lists.newArrayList();
        for (String npcId : playerCurrentRoom.getNpcIds()) {
            StringBuilder sbb = new StringBuilder();
            Npc npcEntity = entityManager.getNpcEntity(npcId);
            if (Main.vowels.contains(Character.toLowerCase(npcEntity.getName().charAt(0)))) {
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

    public void placeItemInRoom(Integer roomId, String itemId) {
        roomManager.getRoom(roomId).addPresentItem(entityManager.getItemEntity(itemId).getItemId());
    }

    public boolean acquireItem(Player player, String itemId) {
        synchronized (interner.intern(itemId)) {
            Stats playerStatsWithEquipmentAndLevel = player.getPlayerStatsWithEquipmentAndLevel();
            if (player.getInventory().size() < playerStatsWithEquipmentAndLevel.getInventorySize()) {
                Item itemEntity = entityManager.getItemEntity(itemId);
                itemEntity.setWithPlayer(true);
                player.addInventoryId(itemId);
                entityManager.saveItem(itemEntity);
                return true;
            } else {
                channelUtils.write(player.getPlayerId(), "Your inventory is full, drop some items to free up room.\r\n");
                return false;
            }
        }
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

    public void roomSay(Integer roomId, String message, String sourcePlayerId) {
        Set<Player> presentPlayers = roomManager.getPresentPlayers(roomManager.getRoom(roomId));
        for (Player player : presentPlayers) {
            if (player.getPlayerId().equals(sourcePlayerId)) {
                channelUtils.write(player.getPlayerId(), message, false);
                continue;
            }
            channelUtils.write(player.getPlayerId(), message, true);
        }
    }

    public String getLookString(Npc npc, Player player) {
        StringBuilder sb = new StringBuilder();
        // passing an empty createState because of the "difference calculation"
        sb.append(Color.MAGENTA + "-+=[ " + Color.RESET).append(npc.getColorName()).append(Color.MAGENTA + " ]=+- " + Color.RESET).append("\r\n");
        ExperienceManager.ExperienceType experienceType = ExperienceManager.getExperienceType(npc, player);
        sb.append(experienceType.getColor());
        sb.append("Level: ").append(npc.getLevel());
        sb.append(Color.RESET);
        sb.append("\r\n");
        sb.append(Color.MAGENTA + "Stats--------------------------------" + Color.RESET).append("\r\n");
        sb.append(buildLookString(npc.getColorName(), npc.getStats(), new StatsBuilder().createStats())).append("\r\n");
        if (npc.getEffects() != null && npc.getEffects().size() > 0) {
            sb.append(Color.MAGENTA + "Effects--------------------------------" + Color.RESET).append("\r\n");
            sb.append(buldEffectsString(npc)).append("\r\n");
        }
        return sb.toString();
    }

    public String buldEffectsString(Npc npc) {
        return renderEffectsString(npc.getEffects());

    }

    public String renderEffectsString(List<Effect> effects) {
        org.nocrala.tools.texttablefmt.Table t = new org.nocrala.tools.texttablefmt.Table(2, BorderStyle.CLASSIC_COMPATIBLE,
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

    public String renderCoolDownString(Set<CoolDown> coolDowns) {
        org.nocrala.tools.texttablefmt.Table t = new org.nocrala.tools.texttablefmt.Table(2, BorderStyle.CLASSIC_COMPATIBLE,
                ShownBorders.NONE);

        t.setColumnWidth(0, 16, 20);
        // t.setColumnWidth(1, 10, 13);

        int i = 1;
        for (CoolDown coolDown : coolDowns) {
            int percent = 100 - (int) (((coolDown.getOriginalNumberOfTicks() - coolDown.getNumberOfTicks()) * 100.0f) / coolDown.getOriginalNumberOfTicks());
            t.addCell(drawProgressBar(percent));
            t.addCell(coolDown.getName());
            i++;
        }
        return t.render();
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

    private String getFormattedNumber(Long longval) {
        return NumberFormat.getNumberInstance(Locale.US).format(longval);
    }

    public void writeToPlayerCurrentRoom(String playerId, String message) {
        if (playerManager.getSessionManager().getSession(playerId).getGrabMultiLineInput().isPresent()) {
            return;
        }
        Player player = playerManager.getPlayer(playerId);
        Room playerCurrentRoom = roomManager.getPlayerCurrentRoom(player).get();
        Set<Player> presentPlayers = roomManager.getPresentPlayers(playerCurrentRoom);
        for (Player presentPlayer : presentPlayers) {
            channelUtils.write(presentPlayer.getPlayerId(), message, true);
        }
    }

    public void writeToRoom(Integer roomId, String message) {
        Room room = roomManager.getRoom(roomId);
        Set<Player> presentPlayers = roomManager.getPresentPlayers(room);
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
            PlayerMetadata playerMetadata = getPlayerManager().getPlayerMetadata(damageEntry.getKey());
            Optional<Room> playerCurrentRoom = getRoomManager().getPlayerCurrentRoom(playerMetadata.getPlayerId());
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
            PlayerMetadata playerMetadata = getPlayerManager().getPlayerMetadata(playerId);
            long amount = damageEntry.getValue();
            double pct = (double) amount / totalDamageDone;
            Player player = getPlayerManager().getPlayer(playerId);
            if (pct >= .90) {
                damagePcts.put(playerId, npc.getPctOFExperience(1, player));
            } else if (pct >= 0.25) {
                damagePcts.put(playerId, npc.getPctOFExperience(.8, player));
            } else if (pct >= 0.10) {
                damagePcts.put(playerId, npc.getPctOFExperience(.5, player));
            } else {
                damagePcts.put(playerId, npc.getPctOFExperience(.25, player));
            }
        }
        return damagePcts;
    }

    public synchronized void removeAllNpcs() {
        for (Npc npc : entityManager.getNpcs().values()) {
            Iterator<Map.Entry<Integer, Room>> rooms = roomManager.getRooms();
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
            sb.append(" ");
            if (player.isActive(CoolDownType.DEATH)) {
                sb.append(Color.RED + "D" + Color.RESET);
            }
            if (player.isActiveForageCoolDown()) {
                sb.append(Color.GREEN + "F" + Color.RESET);
            }
        }
        sb.append(Color.BOLD_ON + Color.WHITE);
        sb.append("] ");
        sb.append(Color.RESET);
        return sb.toString();
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
}

