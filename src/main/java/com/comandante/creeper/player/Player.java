package com.comandante.creeper.player;


import com.codahale.metrics.Meter;
import com.comandante.creeper.Creeper;
import com.comandante.creeper.common.CreeperUtils;
import com.comandante.creeper.core_game.GameManager;
import com.comandante.creeper.core_game.SentryManager;
import com.comandante.creeper.entity.CreeperEntity;
import com.comandante.creeper.items.Effect;
import com.comandante.creeper.items.Equipment;
import com.comandante.creeper.items.EquipmentSlotType;
import com.comandante.creeper.items.ForageManager;
import com.comandante.creeper.items.Item;
import com.comandante.creeper.npc.Npc;
import com.comandante.creeper.npc.NpcStatsChangeBuilder;
import com.comandante.creeper.npc.Temperament;
import com.comandante.creeper.server.player_communication.Color;
import com.comandante.creeper.stats.Levels;
import com.comandante.creeper.stats.Stats;
import com.comandante.creeper.stats.StatsBuilder;
import com.comandante.creeper.stats.StatsHelper;
import com.comandante.creeper.world.model.Room;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import javax.security.auth.Subject;
import java.security.Principal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Player extends CreeperEntity implements Principal {

    private static final Logger log = Logger.getLogger(Player.class);
    private final GameManager gameManager;
    private final String playerId;
    private final Interner<String> interner = Interners.newWeakInterner();
    private final Random random = new Random();
    private String playerName;
    private Channel channel;
    private Optional<String> returnDirection = Optional.empty();
    private Room currentRoom;
    private SortedMap<Long, ActiveFight> activeFights = Collections.synchronizedSortedMap(new TreeMap<Long, ActiveFight>());
    private int tickBucket = 0;
    private int fightTickBucket = 0;
    private final Set<Npc> alertedNpcs = Sets.newHashSet();
    private Optional<Room> previousRoom = Optional.empty();
    private final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(1000);
    private AtomicBoolean isChatMode = new AtomicBoolean(false);

    public static final int FIGHT_TICK_BUCKET_SIZE = 4;


    public Player(String playerName, GameManager gameManager) {
        this.playerName = playerName;
        this.playerId = new String(Base64.encodeBase64(playerName.getBytes()));
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        synchronized (interner.intern(playerId)) {
            try {
                if (processTickBucket(10)) {
                    processRegens();
                    processEffects();
                    if (activeFights.size() > 0) {
                        writePrompt();
                    }
                }
                tickAllActiveCoolDowns();
                activateNextPrimaryActiveFight();
                if (processFightTickBucket(FIGHT_TICK_BUCKET_SIZE)) {
                    processFightRounds();
                }
            } catch (Exception e) {
                log.error("Player ticker failed! + " + playerName, e);
                SentryManager.logSentry(this.getClass(), e, "Player ticker problem!");
            }
        }
    }

    private boolean processTickBucket(int numberOfTicksToFillBucket) {
        if (tickBucket == numberOfTicksToFillBucket) {
            tickBucket = 0;
            return true;
        } else {
            tickBucket = tickBucket + 1;
            return false;
        }
    }

    private boolean processFightTickBucket(int numberOfTicksToFillBucket) {
        if (fightTickBucket == numberOfTicksToFillBucket) {
            fightTickBucket = 0;
            return true;
        } else {
            fightTickBucket = fightTickBucket + 1;
            return false;
        }
    }

    private void processFightRounds() {

        DamageProcessor playerDamageProcesor = getPlayerClass().getDamageProcessor();
        Set<Map.Entry<Long, ActiveFight>> entries = activeFights.entrySet();
        for (Map.Entry<Long, ActiveFight> next : entries) {
            Optional<String> npcIdOptional = next.getValue().getNpcId();
            if (npcIdOptional.isPresent()) {
                // If the NPC has died- bail out.
                String npcId = npcIdOptional.get();
                addCoolDown(new CoolDown(CoolDownType.NPC_FIGHT));
                Npc npc = gameManager.getEntityManager().getNpcEntity(npcId);
                if (npc == null) {
                    continue;
                }
                doFightRound(playerDamageProcesor, npc.getDamageProcessor(), next.getValue());
            }
        }
    }

    private void processRegens() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            Stats stats = getPlayerStatsWithEquipmentAndLevel();
            if (isActive(CoolDownType.NPC_FIGHT) || isActive(CoolDownType.DEATH)) {
                return;
            }
            if (playerMetadata.getStats().getCurrentHealth() < stats.getMaxHealth()) {
                updatePlayerHealth((int) (stats.getMaxHealth() * .05), null);
            }
            if (playerMetadata.getStats().getCurrentMana() < stats.getMaxMana()) {
                addMana((int) (stats.getMaxMana() * .03));
            }
        }
    }

    private void processEffects() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            List<Effect> effectsToRemove = Lists.newArrayList();
            for (Effect effect : playerMetadata.getEffects()) {
                if (effect.getEffectApplications() >= effect.getMaxEffectApplications()) {
                    gameManager.getChannelUtils().write(playerId, Color.BOLD_ON + Color.GREEN + "[effect] " + Color.RESET + effect.getEffectName() + " has worn off.\r\n", true);
                    effectsToRemove.add(effect);
                    continue;
                } else {
                    effect.setEffectApplications(effect.getEffectApplications() + 1);
                    gameManager.getEffectsManager().application(effect, this);
                }

            }
            for (Effect effect : effectsToRemove) {
                playerMetadata.removeEffect(effect);
            }
            savePlayerMetadata(playerMetadata);
        }
    }

    public void killPlayer(Npc npc) {
        resetEffects();
        synchronized (interner.intern(playerId)) {
            if (npc != null && doesActiveFightExist(npc)) {
                removeAllActiveFights();
            }
            if (!isActive(CoolDownType.DEATH)) {
                Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
                if (!playerMetadataOptional.isPresent()) {
                    return;
                }
                PlayerMetadata playerMetadata = playerMetadataOptional.get();
                long newGold = playerMetadata.getGold() / 2;
                playerMetadata.setGold(newGold);
                gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
                if (newGold > 0) {
                    gameManager.getChannelUtils().write(getPlayerId(), "You just " + Color.BOLD_ON + Color.RED + "lost " + Color.RESET + newGold + Color.YELLOW + " gold" + Color.RESET + "!\r\n");
                }
                removeActiveAlertStatus();
                CoolDown death = new CoolDown(CoolDownType.DEATH);
                addCoolDown(death);
                gameManager.writeToPlayerCurrentRoom(getPlayerId(), getPlayerName() + " is now dead." + "\r\n");
                PlayerMovement playerMovement = new PlayerMovement(this, gameManager.getRoomManager().getPlayerCurrentRoom(this).get().getRoomId(), GameManager.LOBBY_ID, "vanished into the ether.", "");
                movePlayer(playerMovement);
                String prompt = gameManager.buildPrompt(playerId);
                gameManager.getChannelUtils().write(getPlayerId(), prompt, true);
            }
        }
    }


    public boolean updatePlayerHealth(long amount, Npc npc) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            if (!playerMetadataOptional.isPresent()) {
                return false;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            if (amount > 0) {
                addHealth(amount, playerMetadata);
                gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
                return false;
            } else {
                Stats stats = playerMetadata.getStats();
                if ((stats.getCurrentHealth() + amount) < 0) {
                    stats.setCurrentHealth(0);
                } else {
                    stats.setCurrentHealth(stats.getCurrentHealth() + amount);
                }
                gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
                if (playerMetadata.getStats().getCurrentHealth() == 0) {
                    killPlayer(npc);
                    return true;
                }
            }
        }
        return false;
    }

    public Optional<Room> getPreviousRoom() {
        synchronized (interner.intern(playerId)) {
            return previousRoom;
        }
    }

    public void setPreviousRoom(Room previousRoom) {
        synchronized (interner.intern(playerId)) {
            this.previousRoom = Optional.ofNullable(previousRoom);
        }
    }

    public void writeMessage(String msg) {
        gameManager.getChannelUtils().write(getPlayerId(), msg);
    }

    public long getAvailableMana() {
        return getPlayerStatsWithEquipmentAndLevel().getCurrentMana();
    }


    private void addHealth(long addAmt, PlayerMetadata playerMetadata) {
        long currentHealth = playerMetadata.getStats().getCurrentHealth();
        Stats statsModifier = getPlayerStatsWithEquipmentAndLevel();
        long maxHealth = statsModifier.getMaxHealth();
        long proposedNewAmt = currentHealth + addAmt;
        if (proposedNewAmt > maxHealth) {
            if (currentHealth < maxHealth) {
                long adjust = proposedNewAmt - maxHealth;
                proposedNewAmt = proposedNewAmt - adjust;
            } else {
                proposedNewAmt = proposedNewAmt - addAmt;
            }
        }
        playerMetadata.getStats().setCurrentHealth(proposedNewAmt);
    }

    public void addMana(long addAmt) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            long currentMana = playerMetadata.getStats().getCurrentMana();
            Stats statsModifier = getPlayerStatsWithEquipmentAndLevel();
            long maxMana = statsModifier.getMaxMana();
            long proposedNewAmt = currentMana + addAmt;
            if (proposedNewAmt > maxMana) {
                if (currentMana < maxMana) {
                    long adjust = proposedNewAmt - maxMana;
                    proposedNewAmt = proposedNewAmt - adjust;
                } else {
                    proposedNewAmt = proposedNewAmt - addAmt;
                }
            }
            playerMetadata.getStats().setCurrentMana(proposedNewAmt);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void addExperience(long exp) {
        synchronized (interner.intern(playerId)) {
            final Meter requests = Creeper.metrics.meter("experience-" + playerName);
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            long currentExperience = playerMetadata.getStats().getExperience();
            long currentLevel = Levels.getLevel(currentExperience);
            playerMetadata.getStats().setExperience(currentExperience + exp);
            requests.mark(exp);
            long newLevel = Levels.getLevel(playerMetadata.getStats().getExperience());
            if (newLevel > currentLevel) {
                gameManager.announceLevelUp(playerName, currentLevel, newLevel);
            }
            savePlayerMetadata(playerMetadata);
        }
    }

    public long getLevel() {
        Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
        return playerMetadataOptional.map(playerMetadata -> Levels.getLevel(playerMetadata.getStats().getExperience())).orElse(0L);
    }

    private Optional<PlayerMetadata> getPlayerMetadata() {
        return gameManager.getPlayerManager().getPlayerMetadata(playerId);
    }

    private void savePlayerMetadata(PlayerMetadata playerMetadata) {
        gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
    }

    public long getCurrentHealth() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = gameManager.getPlayerManager().getPlayerMetadata(playerId);
            return playerMetadataOptional.map(playerMetadata -> playerMetadata.getStats().getCurrentHealth()).orElse(0L);
        }
    }

    public void transferGoldToBank(long amt) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.transferGoldToBank(amt);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void transferBankGoldToPlayer(long amt) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.transferBankGoldToPlayer(amt);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void incrementGold(long amt) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.incrementGold(amt);
            savePlayerMetadata(playerMetadata);
        }
    }

    public boolean addEffect(Effect effect) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return false;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            if (playerMetadata.getEffects() != null && (playerMetadata.getEffects().size() >= playerMetadata.getStats().getMaxEffects())) {
                return false;
            }
            playerMetadata.addEffect(effect);
            savePlayerMetadata(playerMetadata);
            return true;
        }
    }

    public void resetEffects() {
        synchronized (interner) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.resetEffects();
            gameManager.getPlayerManager().savePlayerMetadata(playerMetadata);
        }
    }

    public void addLearnedSpellByName(String spellName) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.addLearnedSpellByName(spellName);
            savePlayerMetadata(playerMetadata);
        }
    }

    public boolean doesHaveSpellLearned(String spellName) {
        Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
        if (!playerMetadataOptional.isPresent()) {
            return false;
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        if (playerMetadata.getLearnedSpells() == null || playerMetadata.getLearnedSpells().length == 0) {
            return false;
        }
        List<String> learnedSpells = Arrays.asList(playerMetadata.getLearnedSpells());
        return learnedSpells.contains(spellName);
    }

    public void removeLearnedSpellByName(String spellName) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.removeLearnedSpellByName(spellName);
            savePlayerMetadata(playerMetadata);
        }
    }

    public List<String> getLearnedSpells() {
        Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
        if (!playerMetadataOptional.isPresent()) {
            return Lists.newArrayList();
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        return Lists.newArrayList(playerMetadata.getLearnedSpells());
    }

    public boolean isActiveAlertNpcStatus(Npc npc) {
        synchronized (interner.intern(playerId)) {
            return alertedNpcs.contains(npc);
        }
    }

    public boolean isActiveAlertNpcStatus() {
        synchronized (interner.intern(playerId)) {
            return alertedNpcs.size() > 0;
        }
    }

    public boolean areAnyAlertedNpcsInCurrentRoom() {
        return currentRoom.getPresentNpcs().stream().filter(this::isActiveAlertNpcStatus).count() > 0;
    }

    public boolean areInTheSameRoom(Npc npc) {
        return currentRoom.getPresentNpcs().contains(npc);
    }

    public void setIsActiveAlertNpcStatus(Npc npc) {
        synchronized (interner.intern(playerId)) {
            alertedNpcs.add(npc);
        }
    }

    public void removeActiveAlertStatus() {
        synchronized (interner.intern(playerId)) {
            alertedNpcs.clear();
        }
    }

    public void removeActiveAlertStatus(Npc npc) {
        synchronized (interner.intern(playerId)) {
            alertedNpcs.clear();
        }
    }

    public Set<Npc> getAlertedNpcs() {
        return alertedNpcs;
    }

    public void dropItem(String itemId) {
        this.dropItem(itemId, false);
    }

    public void dropItem(String itemId, boolean fromApi) {
        Optional<Item> itemToDrop = getInventory().stream().filter(item -> item.getItemId().equals(itemId)).findFirst();
        if (!itemToDrop.isPresent()) {
            return;
        }
        Item item = itemToDrop.get();
        item.setWithPlayer(false);
        gameManager.placeItemInRoom(currentRoom.getRoomId(), item.getItemId());
        removeInventoryId(item.getItemId());
        gameManager.getItemDecayManager().addItem(item);
        gameManager.getEntityManager().saveItem(item);
        gameManager.roomSay(currentRoom.getRoomId(), getPlayerName() + " dropped " + item.getItemName(), playerId, fromApi);
    }

    public void addInventoryId(String inventoryId) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.addInventoryEntityId(inventoryId);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void transferItemToLocker(String inventoryId) {
        synchronized (interner.intern(playerId)) {
            removeInventoryId(inventoryId);
            addLockerInventoryId(inventoryId);
        }
    }

    public void removeInventoryId(String inventoryId) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.removeInventoryEntityId(inventoryId);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void addLockerInventoryId(String entityId) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.addLockerEntityId(entityId);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void addNpcKillLog(String npcName) {
        gameManager.getEventProcessor().assAsyncJob(() -> {
            synchronized (interner.intern(playerId)) {
                Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
                if (!playerMetadataOptional.isPresent()) {
                    return;
                }
                PlayerMetadata playerMetadata = playerMetadataOptional.get();
                playerMetadata.addNpcKill(npcName);
                savePlayerMetadata(playerMetadata);
            }
        });
    }

    public void transferItemFromLocker(String entityId) {
        synchronized (interner.intern(playerId)) {
            if (gameManager.acquireItem(this, entityId)) {
                removeLockerInventoryId(entityId);
            }
        }
    }

    public void removeLockerInventoryId(String lockerInventoryId) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.removeLockerEntityId(lockerInventoryId);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void updatePlayerMana(int amount) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            Stats stats = playerMetadata.getStats();
            stats.setCurrentMana(stats.getCurrentMana() + amount);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void updatePlayerForageExperience(int amount) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            Stats stats = playerMetadata.getStats();
            stats.setForaging(stats.getForaging() + amount);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void addCoolDown(CoolDown coolDown) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.addCoolDown(coolDown);
            savePlayerMetadata(playerMetadata);
        }
    }

    public Set<CoolDown> getCoolDowns() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return Sets.newHashSet();
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            return playerMetadata.getCoolDowns();
        }
    }

    public boolean isActiveCoolDown() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return false;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            return playerMetadata.getCoolDowns().size() > 0;
        }
    }

    public boolean isActiveForageCoolDown() {
        if (isActive(CoolDownType.FORAGE_LONG) ||
                isActive(CoolDownType.FORAGE_MEDIUM) ||
                isActive(CoolDownType.FORAGE_SHORT) ||
                isActive(CoolDownType.FORAGE_SUPERSHORT)) {
            return true;
        }
        return false;
    }

    public boolean isActive(CoolDownType coolDownType) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return false;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            Set<CoolDown> coolDowns = playerMetadata.getCoolDowns();
            for (CoolDown c : coolDowns) {
                if (c.getCoolDownType().equals(coolDownType)) {
                    if (c.isActive()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isActiveSpellCoolDown(String spellName) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return false;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            Set<CoolDown> coolDowns = playerMetadata.getCoolDowns();
            for (CoolDown coolDown : coolDowns) {
                if (coolDown.getName().equalsIgnoreCase(spellName)) {
                    return true;
                }
            }
            return false;
        }
    }

    private void tickAllActiveCoolDowns() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            if (playerMetadata.getCoolDownMap().isEmpty()) {
                return;
            }
            playerMetadata.getCoolDownMap().entrySet().removeIf(coolDownTypeCoolDownEntry -> {
                if (coolDownTypeCoolDownEntry.getValue().isActive()) {
                    coolDownTypeCoolDownEntry.getValue().decrementTick();
                } else {
                    if (coolDownTypeCoolDownEntry.getValue().equals(CoolDownType.DEATH)) {
                        gameManager.getChannelUtils().write(playerId, "You have risen from the dead.\r\n");
                    }
                    return true;
                }
                return false;
            });
            savePlayerMetadata(playerMetadata);
        }
    }

    public void writePrompt() {
        String prompt = gameManager.buildPrompt(playerId);
        gameManager.getChannelUtils().write(playerId, prompt, true);
    }

    public String getPlayerId() {
        return playerId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Optional<String> getReturnDirection() {
        return returnDirection;
    }

    public void setReturnDirection(Optional<String> returnDirection) {
        this.returnDirection = returnDirection;
    }

    public Room getCurrentRoom() {
        Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
        if (currentRoom == null && playerMetadataOptional.isPresent()) {
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            Integer currentRoomId = playerMetadata.getCurrentRoomId();
            if (currentRoomId != null) {
                this.currentRoom = gameManager.getRoomManager().getRoom(currentRoomId);
            }
        }
        return currentRoom;
    }

    public void setCurrentRoomAndPersist(Room currentRoom) {
        // Persisting lazily so that performance doesn't suffer.
        setCurrentRoom(currentRoom);
        gameManager.getEventProcessor().assAsyncJob(() -> {
            synchronized (interner.intern(playerId)) {
                Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
                if (!playerMetadataOptional.isPresent()) {
                    return;
                }
                PlayerMetadata playerMetadata = playerMetadataOptional.get();
                playerMetadata.setCurrentRoomId(currentRoom.getRoomId());
                savePlayerMetadata(playerMetadata);
            }
        });
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public Map<String, Long> getNpcKillLog() {
        ImmutableMap.Builder<String, Long> builder = ImmutableMap.builder();
        Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
        if (!playerMetadataOptional.isPresent()) {
            return Maps.newHashMap();
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        playerMetadata.getNpcKillLog().forEach(builder::put);
        return builder.build();
    }

    public void removePlayerFromRoom(Room room) {
        synchronized (interner.intern(playerId)) {
            room.removePresentPlayer(getPlayerId());
        }
    }

    public boolean meetRoomRequirements(PlayerMovement playerMovement, Room room) {

        if (playerMovement.getPlayer().getPlayerMetadata().get().getPlayerRoleSet().contains(PlayerRole.GOD)) {
            return true;
        }

        if (room.getRequiredInternalItemNames().isEmpty() && !room.getMinimumLevel().isPresent()) {
            return true;
        }

        for (String requiredInternalItemName : room.getRequiredInternalItemNames()) {
            if (!doesItemExistInInventoryOrEquipped(requiredInternalItemName)) {
                writeMessage("In order to enter this room you must possess a " + requiredInternalItemName + ".");
                return false;
            }

        }
        if (room.getMinimumLevel().isPresent()) {
            if (!(getLevel() >= room.getMinimumLevel().get())) {
                writeMessage("In order to enter this room you must be at least level " + room.getMinimumLevel().get() + ".");
                return false;
            }
        }

        return true;
    }

    private boolean doesItemExistInInventoryOrEquipped(String internalItemName) {
        boolean existsInIventory = getInventory()
                .stream()
                .anyMatch(item -> item.getInternalItemName().equals(internalItemName));

        boolean existsInEquipment = getEquipment().stream().anyMatch(item -> item.getInternalItemName().equals(internalItemName));

        return existsInEquipment || existsInIventory;
    }

    public void movePlayer(PlayerMovement playerMovement) {
        synchronized (interner.intern(playerId)) {
            Optional<Room> sourceRoom = Optional.empty();
            if (playerMovement.getSourceRoomId() != null) {
                sourceRoom = Optional.ofNullable(gameManager.getRoomManager().getRoom(playerMovement.getSourceRoomId()));
            }

            Room destinationRoom = gameManager.getRoomManager().getRoom(playerMovement.getDestinationRoomId());

            if (!meetRoomRequirements(playerMovement, destinationRoom)) {
                return;
            }

            if (sourceRoom.isPresent()) {
                removePlayerFromRoom(sourceRoom.get());
                for (Player next : sourceRoom.get().getPresentPlayers()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(playerMovement.getPlayer().getPlayerName());
                    sb.append(" ").append(playerMovement.getRoomExitMessage());
                    gameManager.getChannelUtils().write(next.getPlayerId(), sb.toString(), true);
                }
                setPreviousRoom(currentRoom);
            }

            destinationRoom.addPresentPlayer(playerMovement.getPlayer().getPlayerId());
            setCurrentRoomAndPersist(destinationRoom);
            for (Player next : destinationRoom.getPresentPlayers()) {
                if (next.getPlayerId().equals(playerMovement.getPlayer().getPlayerId())) {
                    continue;
                }
                gameManager.getChannelUtils().write(next.getPlayerId(), playerMovement.getPlayer().getPlayerName() + " arrived.", true);
            }
            setReturnDirection(java.util.Optional.ofNullable(playerMovement.getReturnDirection()));
            gameManager.currentRoomLogic(playerId, gameManager.getRoomManager().getRoom(playerMovement.getDestinationRoomId()));
            processNpcAggro();
        }
    }

    public void processNpcAggro() {
        synchronized (interner.intern(playerId)) {
            if (isActive(CoolDownType.DEATH)) {
                return;
            }
            List<Npc> aggresiveRoomNpcs = currentRoom.getNpcIds().stream()
                    .map(npcId -> gameManager.getEntityManager().getNpcEntity(npcId))
                    .filter(npc -> npc.getTemperament().equals(Temperament.AGGRESSIVE))
                    .filter(npc -> {
                        Npc.NpcLevelColor levelColor = npc.getLevelColor((int) Levels.getLevel(getPlayerStatsWithEquipmentAndLevel().getExperience()));
                        return !levelColor.equals(Npc.NpcLevelColor.WHITE);
                    })
                    .collect(Collectors.toList());

            aggresiveRoomNpcs.forEach(npc -> {
                gameManager.writeToPlayerCurrentRoom(getPlayerId(), getPlayerName() + " has alerted a " + npc.getColorName() + "\r\n");
                gameManager.getChannelUtils().write(playerId, "You can return to your previous location by typing \"back\"" + "\r\n");
                setIsActiveAlertNpcStatus(npc);
                scheduledExecutor.schedule(() -> {
                    removeActiveAlertStatus(npc);
                    if (!areInTheSameRoom(npc)) {
                        return;
                    }
                    if (!npc.getIsAlive().get()) {
                        return;
                    }
                    if (isActive(CoolDownType.DEATH)) {
                        return;
                    }
                    gameManager.writeToPlayerCurrentRoom(getPlayerId(), getPlayerName() + " has " + Color.BOLD_ON + Color.RED + "ANGERED" + Color.RESET + " a " + npc.getColorName() + "\r\n");
                    addActiveFight(npc);
                }, 5, TimeUnit.SECONDS);
            });
        }
    }


    public Optional<Item> getInventoryItemById(String itemId) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return Optional.empty();
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            for (String id : playerMetadata.getInventory()) {
                if (id.equals(itemId)) {
                    return gameManager.getEntityManager().getItemEntity(itemId);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Item> getInventoryItem(String itemKeyword) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return Optional.empty();
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            for (String itemId : playerMetadata.getInventory()) {
                Optional<Item> itemOptional = gameManager.getEntityManager().getItemEntity(itemId);
                if (!itemOptional.isPresent()) {
                    log.info("Orphaned inventoryId:" + itemId + " player: " + getPlayerName());
                    continue;
                }
                Item itemEntity = itemOptional.get();
                if (itemEntity.getItemTriggers().contains(itemKeyword)) {
                    return Optional.of(itemEntity);
                }
            }
            return Optional.empty();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void show(String itemId) {
        Optional<Item> inventoryItemById = getInventoryItemById(itemId);
        if (inventoryItemById.isPresent()) {
            gameManager.writeToPlayerCurrentRoom(playerId, getPlayerName() + " whips out " + inventoryItemById.get().getItemName() + ".\r\n");
        }
    }

    public List<String> getRolledUpLockerInventory() {
        synchronized (interner.intern(playerId)) {
            List<String> rolledUp = Lists.newArrayList();
            List<Item> inventory = getLockerInventory();
            Map<String, Integer> itemAndCounts = Maps.newHashMap();
            if (inventory != null) {
                for (Item item : inventory) {
                    StringBuilder invItem = new StringBuilder();
                    invItem.append(item.getItemName());
                    int maxUses = item.getMaxUses();
                    if (item.getMaxUses() > 0) {
                        int remainingUses = maxUses - item.getNumberOfUses();
                        invItem.append(" - ").append(remainingUses);
                        if (remainingUses == 1) {
                            invItem.append(" use left.");
                        } else {
                            invItem.append(" uses left.");
                        }
                    }
                    if (itemAndCounts.containsKey(invItem.toString())) {
                        Integer integer = itemAndCounts.get(invItem.toString());
                        integer = integer + 1;
                        itemAndCounts.put(invItem.toString(), integer);
                    } else {
                        itemAndCounts.put(invItem.toString(), 1);
                    }
                }
                StringBuilder inventoryLine = new StringBuilder();
                for (Map.Entry<String, Integer> next : itemAndCounts.entrySet()) {
                    if (next.getValue() > 1) {
                        inventoryLine.append(next.getKey()).append(" (").append(next.getValue()).append(")").append("\r\n");
                    } else {
                        inventoryLine.append(next.getKey()).append("\r\n");
                    }
                }
                rolledUp.add(inventoryLine.toString());
            }
            return rolledUp;
        }
    }

    public List<Item> getLockerInventory() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptonal = getPlayerMetadata();
            if (!playerMetadataOptonal.isPresent()) {
                return Lists.newArrayList();
            }
            PlayerMetadata playerMetadata = playerMetadataOptonal.get();
            List<Item> inventoryItems = Lists.newArrayList();
            List<String> inventory = playerMetadata.getLockerInventory();
            if (inventory != null) {
                for (String itemId : inventory) {
                    Optional<Item> itemOptional = gameManager.getEntityManager().getItemEntity(itemId);
                    if (!itemOptional.isPresent()) {
                        log.info("Orphaned inventoryId:" + itemId + " player: " + getPlayerName());
                        continue;
                    }
                    inventoryItems.add(itemOptional.get());
                }
            }
            inventoryItems.sort(Comparator.comparing(Item::getItemName));
            return inventoryItems;
        }
    }

    public List<String> getRolledUpInventory() {
        synchronized (interner.intern(playerId)) {
            List<String> rolledUp = Lists.newArrayList();
            List<Item> inventory = getInventory();
            Map<String, Integer> itemAndCounts = Maps.newHashMap();
            if (inventory != null) {
                for (Item item : inventory) {
                    StringBuilder invItem = new StringBuilder();
                    invItem.append(item.getItemName());
                    int maxUses = item.getMaxUses();
                    if (maxUses > 0) {
                        int remainingUses = maxUses - item.getNumberOfUses();
                        invItem.append(" - ").append(remainingUses);
                        if (remainingUses == 1) {
                            invItem.append(" use left.");
                        } else {
                            invItem.append(" uses left.");
                        }
                    }
                    if (itemAndCounts.containsKey(invItem.toString())) {
                        Integer integer = itemAndCounts.get(invItem.toString());
                        integer = integer + 1;
                        itemAndCounts.put(invItem.toString(), integer);
                    } else {
                        itemAndCounts.put(invItem.toString(), 1);
                    }
                }
                for (Map.Entry<String, Integer> next : itemAndCounts.entrySet()) {
                    StringBuilder inventoryLine = new StringBuilder();
                    if (next.getValue() > 1) {
                        inventoryLine.append(next.getKey()).append(" (").append(next.getValue()).append(")");
                    } else {
                        inventoryLine.append(next.getKey());
                    }
                    rolledUp.add(inventoryLine.toString());
                }
            }
            return rolledUp;
        }
    }

    public List<Item> getInventory() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return Lists.newArrayList();
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            List<Item> inventoryItems = Lists.newArrayList();
            List<String> inventory = playerMetadata.getInventory();
            if (inventory != null) {
                for (String itemId : inventory) {
                    Optional<Item> itemOptional = gameManager.getEntityManager().getItemEntity(itemId);
                    if (!itemOptional.isPresent()) {
                        log.info("Orphaned inventoryId:" + itemId + " player: " + getPlayerName());
                        continue;
                    }
                    inventoryItems.add(itemOptional.get());
                }
            }
            inventoryItems.sort(Comparator.comparing(Item::getItemName));
            return inventoryItems;
        }
    }

    public Set<Item> getEquipment() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return Sets.newHashSet();
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            Set<Item> equipmentItems = Sets.newHashSet();
            String[] equipment = playerMetadata.getPlayerEquipment();
            if (equipment != null) {
                for (String itemId : equipment) {
                    Optional<Item> itemOptional = gameManager.getEntityManager().getItemEntity(itemId);
                    if (!itemOptional.isPresent()) {
                        log.info("Orphaned equipmentId:" + itemId + " player: " + getPlayerName());
                        continue;
                    }
                    equipmentItems.add(itemOptional.get());
                }
            }
            return equipmentItems;
        }
    }

    public void equip(Item item) {
        this.equip(item, false);
    }

    public void equip(Item item, boolean isApi) {
        synchronized (interner.intern(playerId)) {
            if (item.getEquipment() == null) {
                return;
            }
            Equipment equipment = item.getEquipment();
            if (equipment.getAllowedPlayerClass().size() > 0) {
                if (!equipment.getAllowedPlayerClass().contains(getPlayerClass())) {
                    gameManager.getChannelUtils().write(playerId, "Your class is unable to equip " + item.getItemName() + "\r\n", isApi);
                    return;
                }
            }

            EquipmentSlotType equipmentSlotType = equipment.getEquipmentSlotType();
            Optional<Item> slotItemOptional = getSlotItem(equipmentSlotType);
            if (slotItemOptional.isPresent()) {
                if (!unEquip(slotItemOptional.get(), isApi)) {
                    return;
                }
            }
            gameManager.getChannelUtils().write(playerId, "Equipping " + item.getItemName() + "\r\n", isApi);
            addEquipmentId(item.getItemId());
            removeInventoryId(item.getItemId());
        }
    }

    public Optional<Item> getSlotItem(EquipmentSlotType slot) {
        Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
        if (!playerMetadataOptional.isPresent()) {
            return Optional.empty();
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        if (playerMetadata.getPlayerEquipment() == null) {
            return Optional.empty();
        }
        for (String item : playerMetadata.getPlayerEquipment()) {
            Optional<Item> itemOptional = gameManager.getEntityManager().getItemEntity(item);
            if (!itemOptional.isPresent()) {
                continue;
            }
            Item itemEntity = itemOptional.get();
            EquipmentSlotType equipmentSlotType = itemEntity.getEquipment().getEquipmentSlotType();
            if (equipmentSlotType.equals(slot)) {
                return Optional.of(itemEntity);
            }
        }
        return Optional.empty();
    }

    public boolean unEquip(Item item) {
        return this.unEquip(item, false);
    }

    public boolean unEquip(Item item, Boolean isApi) {
        synchronized (interner.intern(playerId)) {
            gameManager.getChannelUtils().write(playerId, "Un-equipping " + item.getItemName() + "\r\n", isApi);
            if (gameManager.acquireItem(this, item.getItemId())) {
                removeEquipmentId(item.getItemId());
                return true;
            }
            return false;
        }
    }

    public void addEquipmentId(String equipmentId) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.addEquipmentEntityId(equipmentId);
            savePlayerMetadata(playerMetadata);
        }
    }

    public void removeEquipmentId(String equipmentId) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.removeEquipmentEntityId(equipmentId);
            savePlayerMetadata(playerMetadata);
        }
    }

    public String getLookString() {
        StringBuilder sb = new StringBuilder();
        Stats origStats = gameManager.getStatsModifierFactory().getStatsModifier(this);
        Stats modifiedStats = getPlayerStatsWithEquipmentAndLevel();
        Stats diffStats = StatsHelper.getDifference(modifiedStats, origStats);
        sb.append(Color.MAGENTA)
                .append("-+=[ ").append(Color.RESET).append(playerName).append(Color.MAGENTA + " ]=+- " + Color.RESET)
                .append("\r\n");
        sb.append("Level ").append(Levels.getLevel(origStats.getExperience())).append(" ")
                .append(Color.YELLOW).append("[").append(Color.RESET).append(CreeperUtils.capitalize(getPlayerClass().getIdentifier())).append(Color.YELLOW).append("]").append(Color.RESET)
                .append("\r\n");
        sb.append("Foraging Level ").append(ForageManager.getLevel(modifiedStats.getForaging())).append("\r\n");
        sb.append(Color.MAGENTA + "Equip--------------------------------" + Color.RESET).append("\r\n");
        sb.append(buildEquipmentString()).append("\r\n");
        sb.append(Color.MAGENTA + "Stats--------------------------------" + Color.RESET).append("\r\n");
        sb.append(gameManager.buildLookString(playerName, modifiedStats, diffStats)).append("\r\n");
        Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
        if (playerMetadataOptional.isPresent()) {
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            if (playerMetadata.getEffects() != null && playerMetadata.getEffects().size() > 0) {
                sb.append(Color.MAGENTA + "Effects--------------------------------" + Color.RESET).append("\r\n");
                sb.append(buldEffectsString()).append("\r\n");
            }
        }
        StringBuilder finalString = new StringBuilder();
        Lists.newArrayList(sb.toString().split("[\\r\\n]+")).forEach(s -> finalString.append(CreeperUtils.trimTrailingBlanks(s)).append("\r\n"));
        return finalString.toString();
    }

    public Stats getPlayerStatsWithEquipmentAndLevel() {
        synchronized (interner.intern(playerId)) {
            StatsBuilder statsBuilder = new StatsBuilder();
            Stats newStats = statsBuilder.createStats();

            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return newStats;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            Stats playerStats = gameManager.getStatsModifierFactory().getStatsModifier(this);
            StatsHelper.combineStats(newStats, playerStats);
            String[] playerEquipment = playerMetadata.getPlayerEquipment();
            if (playerEquipment == null) {
                return playerStats;
            }
            for (String equipId : playerEquipment) {
                Optional<Item> itemOptional = gameManager.getEntityManager().getItemEntity(equipId);
                if (!itemOptional.isPresent()) {
                    continue;
                }
                Item itemEntity = itemOptional.get();
                Equipment equipment = itemEntity.getEquipment();
                Stats stats = equipment.getStats();
                StatsHelper.combineStats(newStats, stats);
            }
            if (playerMetadata.getEffects() != null) {
                for (Effect effect : playerMetadata.getEffects()) {
                    StatsHelper.combineStats(newStats, effect.getDurationStats());
                }
            }
            return newStats;
        }
    }

    public PlayerClass getPlayerClass() {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return PlayerClass.BASIC;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            PlayerClass playerClass = playerMetadata.getPlayerClass();
            if (playerClass == null) {
                return PlayerClass.BASIC;
            }
            return playerClass;
        }
    }

    public void setPlayerClass(PlayerClass playerClass) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.setPlayerClass(playerClass);
            savePlayerMetadata(playerMetadata);
        }
    }

    public String buildEquipmentString() {
        Table t = new Table(2, BorderStyle.CLASSIC_COMPATIBLE,
                ShownBorders.NONE);
        t.setColumnWidth(0, 16, 20);

        List<EquipmentSlotType> all = EquipmentSlotType.getAll();
        for (EquipmentSlotType slot : all) {
            t.addCell(capitalize(slot.getName()));
            Optional<Item> slotItemOptional = getSlotItem(slot);
            if (slotItemOptional.isPresent()) {
                t.addCell(slotItemOptional.get().getItemName());
            } else {
                t.addCell("");
            }
        }
        return t.render();
    }

    /* FIGHT FIGHT FIGHT FIGHT */

    public String buldEffectsString() {
        Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
        if (!playerMetadataOptional.isPresent()) {
            return "";
        }
        PlayerMetadata playerMetadata = playerMetadataOptional.get();
        List<Effect> effects = playerMetadata.getEffects();
        return gameManager.renderEffectsString(effects);
    }

    private String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public void removeAllActiveFights() {
        synchronized (interner.intern(playerId)) {
            Iterator<Map.Entry<Long, ActiveFight>> iterator = activeFights.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, ActiveFight> next = iterator.next();
                iterator.remove();
            }
        }
    }

    public boolean setPlayerSetting(String key, String value) {
        boolean success;
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return false;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            success = playerMetadata.setSetting(key, value);
            savePlayerMetadata(playerMetadata);
        }
        return success;
    }

    public Optional<String> getPlayerSetting(String key) {
        return getPlayerMetadata().flatMap(playerMetadata -> Optional.ofNullable(playerMetadata.getSetting(key)));
    }

    public void removePlayerSetting(String key) {
        synchronized (interner.intern(playerId)) {
            Optional<PlayerMetadata> playerMetadataOptional = getPlayerMetadata();
            if (!playerMetadataOptional.isPresent()) {
                return;
            }
            PlayerMetadata playerMetadata = playerMetadataOptional.get();
            playerMetadata.deleteSetting(key);
            savePlayerMetadata(playerMetadata);
        }
    }

    public Map<String, String> getPlayerSettings() {
        return getPlayerMetadata().map(PlayerMetadata::getPlayerSettings).orElseGet(Maps::newHashMap);
    }

    public boolean addActiveFight(Npc npc) {
        synchronized (interner.intern(playerId)) {
            if (gameManager.getEntityManager().getNpcEntity(npc.getEntityId()) != null) {
                if (!doesActiveFightExist(npc)) {
                    addCoolDown(new CoolDown(CoolDownType.NPC_FIGHT));
                    ActiveFight activeFight = ActiveFight.builder()
                            .npcId(npc.getEntityId())
                            .isPrimary(false)
                            .create();
                    activeFights.put(System.nanoTime(), activeFight);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean doesActiveFightExist(Npc npc) {
        synchronized (interner.intern(playerId)) {
            if (gameManager.getEntityManager().getNpcEntity(npc.getEntityId()) == null) {
                removeActiveFight(npc);
            }
            for (Map.Entry<Long, ActiveFight> entry : activeFights.entrySet()) {
                ActiveFight fight = entry.getValue();
                Optional<String> npcIdOptional = fight.getNpcId();
                if (npcIdOptional.isPresent() && npcIdOptional.get().equals(npc.getEntityId())) {
                    return true;
                }
            }
            return false;
        }
    }

    public void removeActiveFight(Npc npc) {
        synchronized (interner.intern(playerId)) {
            Iterator<Map.Entry<Long, ActiveFight>> iterator = activeFights.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, ActiveFight> next = iterator.next();
                if (next.getValue().getNpcId().equals(npc.getEntityId())) {
                    if (next.getValue().isPrimary()) {
                    }
                    iterator.remove();
                }
            }
        }
    }

    public boolean isActiveFights() {
        synchronized (interner.intern(playerId)) {
            if (activeFights.size() > 0) {
                // Remove any fights with dead NPCs that no longer exist in Entity Manager.
                activeFights.entrySet().removeIf(next -> next.getValue().getNpcId().isPresent() && gameManager.getEntityManager().getNpcEntity(next.getValue().getNpcId().get()) == null);
            }
        }
        return activeFights.size() > 0;
    }

    public boolean isValidPrimaryActiveFight(Npc npc) {
        synchronized (interner.intern(playerId)) {
            for (Map.Entry<Long, ActiveFight> entry : activeFights.entrySet()) {
                ActiveFight fight = entry.getValue();
                Optional<String> npcIdOptional = fight.getNpcId();
                if (npcIdOptional.isPresent() && fight.getNpcId().get().equals(npc.getEntityId()) && fight.isPrimary()) {
                    return true;
                }
            }
            return false;
        }
    }

    public Optional<ActiveFight> getPrimaryActiveFight() {
        synchronized (interner.intern(playerId)) {
            for (Map.Entry<Long, ActiveFight> entry : activeFights.entrySet()) {
                ActiveFight fight = entry.getValue();
                if (fight.isPrimary()) {
                    return Optional.of(fight);
                }
            }
            return Optional.empty();
        }
    }

    public void activateNextPrimaryActiveFight() {
        synchronized (interner.intern(playerId)) {
            if (!getPrimaryActiveFight().isPresent()) {
                if (activeFights.size() > 0) {
                    activeFights.get(activeFights.firstKey()).setIsPrimary(true);
                }
            }
        }
    }

    private void doFightRound(DamageProcessor playerDamageProcessor, DamageProcessor
            npcDamageProcessor, ActiveFight activeFight) {
        removeActiveAlertStatus();

        // IF FIGHTING NPC
        Optional<String> npcIdOptional = activeFight.getNpcId();
        if (npcIdOptional.isPresent()) {
            String npcId = npcIdOptional.get();
            Npc npc = gameManager.getEntityManager().getNpcEntity(npcId);
            if (npc == null) {
                return;
            }

            NpcStatsChangeBuilder npcStatsChangeBuilder = new NpcStatsChangeBuilder().setPlayer(this);
            if (this.isValidPrimaryActiveFight(npc)) {
                applyItemAttackEffects(this, npc);
                calculatePlayerDamageToNpc(playerDamageProcessor, npc, npcStatsChangeBuilder);
            }

            if (this.doesActiveFightExist(npc)) {
                calculateNpcDamageToPlayer(npcDamageProcessor, npc, npcStatsChangeBuilder);
            }
        }

        // IF FIGHTING PLAYER?
    }

    public boolean attackEffectApplyChange(double percent) {
        double rangeMin = 0;
        double rangeMax = 100;
        double randomValue = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
        return randomValue <= percent;
    }

    private List<Effect> calculateChanceToApplyEffects(Map<Double, Effect> attackEffects) {
        List<Effect> apply = Lists.newArrayList();
        for (Map.Entry<Double, Effect> attackEffect : attackEffects.entrySet()) {
            if (attackEffectApplyChange(attackEffect.getKey())) {
                apply.add(attackEffect.getValue());
            }
        }
        return apply;
    }

    private Effect modifyEffectForPlayer(Player player, Npc npc, Effect origEffect) {
        // ApplyStatsOnTickModifications
        Effect effect = new Effect(origEffect);
        long healthAmount = Math.abs(effect.getApplyStatsOnTick().getCurrentHealth());
        if (healthAmount > 0) {
            healthAmount = (healthAmount * Math.round(player.getPlayerStatsWithEquipmentAndLevel().getIntelligence() * .2d)) - npc.getStats().getWillpower();
            if (npc.getStats().getWillpower() < 0) {
                healthAmount = 0;
            }
            effect.getApplyStatsOnTick().setCurrentHealth(-healthAmount);
        }

        // DurationStatsModifications
        return effect;
    }

    private void applyItemAttackEffects(Player player, Npc npc) {
        Set<Item> equipment = player.getEquipment();
        for (Item item : equipment) {
            Map<Double, Effect> attackEffects = item.getAttackEffects();
            if (attackEffects == null) {
                continue;
            }
            List<Effect> applyEffects = calculateChanceToApplyEffects(item.getAttackEffects());
            for (Effect applyEffect : applyEffects) {
                if (npc.getEffects().stream()
                        .filter(effect -> effect.getEffectName().equals(applyEffect.getEffectName()))
                        .collect(Collectors.toList()).size() > 0) {
                    continue;
                }
                gameManager.getEffectsManager().applyEffectsToNpcs(player, Sets.newHashSet(npc), Sets.newHashSet(modifyEffectForPlayer(this, npc, applyEffect)));
            }
        }
    }

    private void calculatePlayerDamageToNpc(DamageProcessor playerDamageProcessor, Npc npc, NpcStatsChangeBuilder
            npcStatsChangeBuilder) {
        long damageToVictim = 0;
        long chanceToHit = playerDamageProcessor.getChanceToHit(this, npc);
        if (randInt(0, 100) < chanceToHit) {
            damageToVictim = playerDamageProcessor.getAttackAmount(this, npc);
        }
        if (damageToVictim > 0) {
            if (randInt(0, 100) > (100 - playerDamageProcessor.getCriticalChance(this, npc))) {
                long criticalDamage = damageToVictim * 3;
                final String fightMsg = Color.BOLD_ON + Color.RED + "[attack] " + Color.RESET + Color.YELLOW + "The " + npc.getColorName() + " was caught off guard by the attack! " + "+" + NumberFormat.getNumberInstance(Locale.US).format(criticalDamage) + Color.RESET + Color.BOLD_ON + Color.RED + " DAMAGE" + Color.RESET + " done to " + npc.getColorName();
                npcStatsChangeBuilder.setStats(new StatsBuilder().setCurrentHealth(-(criticalDamage)).createStats());
                npcStatsChangeBuilder.setDamageStrings(Collections.singletonList(fightMsg));
            } else {
                final String fightMsg = Color.BOLD_ON + Color.RED + "[attack] " + Color.RESET + Color.YELLOW + "+" + NumberFormat.getNumberInstance(Locale.US).format(damageToVictim) + Color.RESET + Color.BOLD_ON + Color.RED + " DAMAGE" + Color.RESET + " done to " + npc.getColorName();
                npcStatsChangeBuilder.setStats(new StatsBuilder().setCurrentHealth(-damageToVictim).createStats());
                npcStatsChangeBuilder.setDamageStrings(Collections.singletonList(fightMsg));
            }
        } else {
            final String fightMsg = Color.BOLD_ON + Color.RED + "[attack] " + Color.RESET + "You MISS " + npc.getName() + "!";
            npcStatsChangeBuilder.setStats(new StatsBuilder().setCurrentHealth(-damageToVictim).createStats());
            npcStatsChangeBuilder.setDamageStrings(Collections.singletonList(fightMsg));
        }
    }

    private void calculateNpcDamageToPlayer(DamageProcessor npcDamageProcessor, Npc npc, NpcStatsChangeBuilder
            npcStatsChangeBuilder) {
        int chanceToHitBack = npcDamageProcessor.getChanceToHit(this, npc);
        long damageBack = npcDamageProcessor.getAttackAmount(this, npc);
        if (randInt(0, 100) < chanceToHitBack) {
            final String fightMsg = Color.BOLD_ON + Color.RED + "[attack] " + Color.RESET + npc.buildAttackMessage(this.getPlayerName()) + " -" + NumberFormat.getNumberInstance(Locale.US).format(damageBack) + Color.RESET;
            npcStatsChangeBuilder.setPlayerStatsChange(new StatsBuilder().setCurrentHealth(-damageBack).createStats());
            npcStatsChangeBuilder.setPlayerDamageStrings(Collections.singletonList(fightMsg));

        } else {
            final String fightMsg = Color.BOLD_ON + Color.RED + "[attack] " + Color.RESET + npc.getColorName() + Color.BOLD_ON + Color.CYAN + " MISSES" + Color.RESET + " you!";
            npcStatsChangeBuilder.setPlayerStatsChange(new StatsBuilder().setCurrentHealth(0).createStats());
            npcStatsChangeBuilder.setPlayerDamageStrings(Collections.singletonList(fightMsg));
        }
        npc.addNpcDamage(npcStatsChangeBuilder.createNpcStatsChange());
    }

    public SortedMap<Long, ActiveFight> getActiveFights() {
        return activeFights;
    }

    private int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    public Interner<String> getInterner() {
        return interner;
    }


    public boolean toggleChat() {
        synchronized (interner.intern(playerId)) {
            if (isChatModeOn()) {
                setNotIsChatMode();
                return false;
            } else {
                setIsChatMode();
                return true;
            }
        }
    }

    public void setIsChatMode() {
        this.isChatMode.compareAndSet(false, true);
    }

    public void setNotIsChatMode() {
        this.isChatMode.compareAndSet(true, false);
    }

    public boolean isChatModeOn() {
        return isChatMode.get();
    }

    public CreeperClientStatusBarDetails getClientStatusBarDetails() {
        final boolean isFight = isActiveFights();
        final Stats stats = getPlayerStatsWithEquipmentAndLevel();
        final long currentHealth = stats.getCurrentHealth();
        final long maxHealth = stats.getMaxHealth();
        final long currentMana = stats.getCurrentMana();
        final long maxMana = stats.getMaxMana();
        final long goldAmount = getPlayerMetadata().get().getGold();
        final long goldInBankAmmount = getPlayerMetadata().get().getGoldInBank();
        final List<String> coolDowns = getCoolDowns()
                .stream()
                .map(coolDown -> coolDown.getCoolDownType().getName())
                .collect(Collectors.toList());

        return new CreeperClientStatusBarDetails(currentHealth, maxHealth, currentMana, maxMana, coolDowns, goldAmount, goldInBankAmmount, isFight);
    }

    @Override
    public String getName() {
        return playerName;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    public Optional<Double> getXpModifier(Set<CoolDown> coolDowns) {
        for (CoolDown coolDown : coolDowns) {
            if (coolDown.getCoolDownType().equals(CoolDownType.FIRE_SAUCE)) {
                return Optional.of(1.5);
            }
        }
        return Optional.empty();
    }
}
