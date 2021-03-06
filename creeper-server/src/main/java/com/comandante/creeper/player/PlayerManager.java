package com.comandante.creeper.player;


import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.comandante.creeper.Creeper;
import com.comandante.creeper.core_game.SentryManager;
import com.comandante.creeper.core_game.SessionManager;
import com.comandante.creeper.entity.EntityManager;
import com.comandante.creeper.events.CreeperEvent;
import com.comandante.creeper.events.CreeperEventType;
import com.comandante.creeper.events.PlayerData;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.merchant.Merchant;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcManager;
import com.comandante.creeper.stats.Levels;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.storage.CreeperStorage;
import com.comandante.creeper.world.model.Room;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import events.ListenerService;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.codahale.metrics.MetricRegistry.name;
import static com.comandante.creeper.command.commands.XpCommand.round;
import static com.comandante.creeper.entity.EntityManager.SLEEP_MILLIS;

public class PlayerManager {

    private static final Logger log = Logger.getLogger(PlayerManager.class);

    private final CreeperStorage creeperStorage;
    private final NpcManager npcManager;
    private final SessionManager sessionManager;
    private final ListenerService listenerService;
    private final ConcurrentHashMap<String, Player> players = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public PlayerManager(CreeperStorage creeperStorage, NpcManager npcManager, SessionManager sessionManager, ListenerService listenerService, ObjectMapper objectMapper) {
        this.creeperStorage = creeperStorage;
        this.npcManager = npcManager;
        this.sessionManager = sessionManager;
        this.listenerService = listenerService;
        this.objectMapper = objectMapper;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void emitPlayerInformationEvent(String playerId) {
        PlayerMetadata playerMetadata = getPlayerMetadata(playerId).get();
        Player player = getPlayer(playerId);
        if (player == null) {
            return;
        }
        try {
            PlayerMetadata playerMetadataCopy = new PlayerMetadata(playerMetadata);
            playerMetadataCopy.setPassword("");
            long expToNextLevel = Levels.getXp(Levels.getLevel(playerMetadata.getStats().getExperience())) - playerMetadata.getStats().getExperience();
            long level = Levels.getLevel(playerMetadata.getStats().getExperience());

            Map<String, Item> itemMap = Maps.newLinkedHashMap();
            List<Item> inventory = player.getInventory();
            inventory.forEach(new Consumer<Item>() {
                @Override
                public void accept(Item item) {
                    itemMap.put(item.getItemId(), item);
                }
            });

            Map<String, Item> equipmentMap = Maps.newLinkedHashMap();
            Set<Item> equipment = player.getEquipment();
            for (Item q : equipment) {
                equipmentMap.put(q.getItemId(), q);
            }

            List<Npc> currentRoomNpcs = player.getCurrentRoom().getPresentNpcs();

            Map<String, String> presentNpcs = Maps.newLinkedHashMap();
            currentRoomNpcs.forEach(npc -> presentNpcs.put(npc.getEntityId(), npc.getColorName()));

            List<Item> presentItems = player.getCurrentRoom().getPresentItems();
            Map<String, String> presentPlayers = Maps.newLinkedHashMap();
            player.getCurrentRoom().getPresentPlayers().stream()
                    .forEach(player1 -> presentPlayers.put(player1.getPlayerId(), player1.getPlayerName()));

            Map<String, String> presentMerchants = Maps.newHashMap();
            Set<Merchant> merchants = player.getCurrentRoom().getMerchants();
            merchants.forEach(merchant -> presentMerchants.put(merchant.getColorName(), merchant.getInternalName()));

            String activeFightNpcId = null;
            Double activeFightNpcHealthPercentage = null;
            if (player.getPrimaryActiveFight().isPresent() && player.getPrimaryActiveFight().get().getNpcId().isPresent()) {
                activeFightNpcId = player.getPrimaryActiveFight().get().getNpcId().get();
                Npc npcEntity = npcManager.getNpc(activeFightNpcId);
                if (npcEntity != null) {
                    try {
                        activeFightNpcHealthPercentage = ((double) npcEntity.getStats().getCurrentHealth() / npcEntity.getStats().getMaxHealth()) * 100;
                    } catch (Exception ignored) { }
                }
            }

            Meter meter = Creeper.metrics.meter("experience-" + player.getPlayerName());
            String xpRatePerSecondOverOneMinutes = String.valueOf(round(meter.getOneMinuteRate()));

            PlayerData playerData = new PlayerData(playerMetadata,
                    level,
                    expToNextLevel,
                    player.isActiveFights(),
                    activeFightNpcId,
                    activeFightNpcHealthPercentage,
                    player.getPlayerStatsWithEquipmentAndLevel(),
                    player.getCurrentRoom().getRoomId(),
                    player.getCurrentRoom().getAreas(),
                    player.getLookString(),
                    player.getOrnatePlayerName(),
                    player.getLevelAndClassString(),
                    player.getRolledUpInventory(),
                    itemMap,
                    equipmentMap,
                    presentPlayers,
                    Sets.newLinkedHashSet(presentItems),
                    presentNpcs,
                    presentMerchants,
                    xpRatePerSecondOverOneMinutes);
            CreeperEvent build = new CreeperEvent.Builder()
                    .audience(CreeperEvent.Audience.PLAYER_ONLY)
                    .creeperEventType(CreeperEventType.PLAYERDATA)
                    .epochTimestamp(System.currentTimeMillis())
                    .payload(objectMapper.writeValueAsString(playerData))
                    .playerId(playerMetadata.getPlayerId())
                    .build();
            listenerService.post(build);
        } catch (Exception e) {
            log.error("Problem emitting playermetadata event. Exception: ", e);
        }
    }

    public void savePlayerMetadata(PlayerMetadata playerMetadata) {
        creeperStorage.savePlayerMetadata(playerMetadata);
        emitPlayerInformationEvent(playerMetadata.getPlayerId());
    }

    public Player addPlayer(Player player) {
        return players.put(player.getPlayerId(), player);
    }

    public Iterator<java.util.Map.Entry<String, Player>> getPlayers() {
        return players.entrySet().iterator();
    }

    public Map<String, Player> getAllPlayersMap() {
        return players;
    }

    public void removePlayer(String username) {
        Player player = getPlayerByUsername(username);
        if (player.getChannel() != null && player.getChannel().isConnected()) {
            player.getChannel().disconnect();
        }
        players.remove(player.getPlayerId());
    }

    public Player getPlayerByUsername(String username) {
        return getPlayer(new String(Base64.encodeBase64(username.getBytes())));
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public Optional<Player> getPlayerByCommandTarget(Room room, String target) {
        Set<Player> presentPlayers = room.getPresentPlayers();
        for (Player presentPlayer : presentPlayers) {
            if (presentPlayer != null && presentPlayer.getPlayerName().equals(target)) {
                return Optional.of(presentPlayer);
            }
        }
        return Optional.empty();
    }

    public boolean doesPlayerExist(String username) {
        return players.containsKey(new String(Base64.encodeBase64(username.getBytes())));
    }

    public boolean hasRole(Player player, PlayerRole playerRole) {
        Optional<PlayerMetadata> playerMetadata = getPlayerMetadata(player.getPlayerId());
        if (!playerMetadata.isPresent()) {
            return false;
        }
        Set<PlayerRole> playerRoleSet = playerMetadata.get().getPlayerRoleSet();
        return playerRoleSet != null && playerMetadata.get().getPlayerRoleSet().contains(playerRole);
    }

    public Optional<PlayerMetadata> getPlayerMetadata(String playerId) {
        Optional<PlayerMetadata> playerMetadataOptional = creeperStorage.getPlayerMetadata(playerId);
        return playerMetadataOptional.map(PlayerMetadata::new);
    }

    public boolean hasAnyOfRoles(Player player, Set<PlayerRole> checkRoles) {
        Optional<PlayerMetadata> playerMetadata = getPlayerMetadata(player.getPlayerId());
        if (!playerMetadata.isPresent()) {
            return false;
        }
        Set<PlayerRole> playerRoleSet = playerMetadata.get().getPlayerRoleSet();
        if (playerRoleSet != null) {
            for (PlayerRole checkRole : checkRoles) {
                if (playerRoleSet.contains(checkRole)) {
                    return true;
                }
            }
        } else {
            return false;
        }
        return false;
    }

    public void createAllGauges() {
        for (Map.Entry<String, PlayerMetadata> next : creeperStorage.getAllPlayerMetadata().entrySet()) {
            createGauges(next.getValue());
        }
    }

    public void createGauges(final PlayerMetadata playerMetadata) {
        String playerId = playerMetadata.getPlayerId();
        String guageName = name(PlayerManager.class, playerMetadata.getPlayerName(), "gold");
        if (!doesGaugeExist(guageName)) {
            Creeper.metrics.register(guageName,
                    (Gauge<Long>) () -> {
                        Optional<PlayerMetadata> playerMetadataOpt = creeperStorage.getPlayerMetadata(playerId);
                        return playerMetadataOpt.map(PlayerMetadata::getGold).orElse(0L);
                    });
        }

        guageName = name(PlayerManager.class, playerMetadata.getPlayerName(), "current-health");
        if (!doesGaugeExist(guageName)) {
            Creeper.metrics.register(name(PlayerManager.class, playerMetadata.getPlayerName(), "current-health"),
                    (Gauge<Long>) () -> {
                        Optional<PlayerMetadata> playerMetadataOpt = creeperStorage.getPlayerMetadata(playerId);
                        return playerMetadataOpt.map(PlayerMetadata::getStats).map(Stats::getCurrentHealth).orElse(0L);
                    });
        }

        guageName = name(PlayerManager.class, playerMetadata.getPlayerName(), "xp");
        if (!doesGaugeExist(guageName)) {
            Creeper.metrics.register(name(PlayerManager.class, playerMetadata.getPlayerName(), "xp"),
                    (Gauge<Long>) () -> {
                        Optional<PlayerMetadata> playerMetadataOpt = creeperStorage.getPlayerMetadata(playerId);
                        return playerMetadataOpt.map(PlayerMetadata::getStats).map(Stats::getExperience).orElse(0L);
                    });
        }
    }

    private boolean doesGaugeExist(String name) {
        return Creeper.metrics.getGauges().containsKey(name);
    }

    public Map<String, PlayerMetadata> getPlayerMetadataStore() {
        return creeperStorage.getAllPlayerMetadata();
    }
}
