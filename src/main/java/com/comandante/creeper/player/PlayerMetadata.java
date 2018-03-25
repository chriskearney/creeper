package com.comandante.creeper.player;


import com.comandante.creeper.items.Effect;
import com.comandante.creeper.stats.Stats;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerMetadata implements Serializable {

    private final String playerName;
    private String password;
    private final String playerId;
    private Stats stats;
    private List<String> inventory;
    private List<String> lockerInventory;
    private long gold;
    private long goldInBank;
    private Set<PlayerRole> playerRoleSet;
    private String[] playerEquipment;
    private List<Effect> effects;
    private boolean isMarkedForDelete;
    private Map<String, String> playerSettings;
    private String[] learnedSpells;
    private Map<String, Long> npcKillLog;
    private PlayerClass playerClass;
    private Map<CoolDownType, CoolDown> coolDowns;
    private Integer currentRoomId;
    private Set<String> completedQuests;
    private Map<String, Quest> acceptedQuests;


    public PlayerMetadata(String playerName,
                          String password,
                          String playerId,
                          Stats stats,
                          int gold,
                          Set<PlayerRole> playerRoleSet,
                          String[] playerEquipment,
                          int goldInBank,
                          String[] learnedSpells,
                          Map<String, Long> npcKillLog,
                          PlayerClass playerClass,
                          Map<CoolDownType, CoolDown> coolDowns,
                          Integer currentRoomId,
                          Set<String> completedQuests,
                          Map<String, Quest> acceptedQuests) {
        this.playerName = playerName;
        this.password = password;
        this.playerId = playerId;
        this.stats = stats;
        this.gold = gold;
        this.playerRoleSet = playerRoleSet;
        this.playerEquipment = playerEquipment;
        this.goldInBank = goldInBank;
        this.learnedSpells = learnedSpells;
        this.npcKillLog = npcKillLog;
        this.playerClass = playerClass;
        this.coolDowns = coolDowns;
        this.currentRoomId = currentRoomId;
        this.completedQuests = completedQuests;
        this.acceptedQuests = acceptedQuests;
    }

    public PlayerMetadata(PlayerMetadata playerMetadata) {
        this.playerName = playerMetadata.playerName;
        this.password = playerMetadata.password;
        this.playerId = playerMetadata.playerId;
        this.stats = new Stats(playerMetadata.stats);
        if (playerMetadata.inventory != null) {
            this.inventory = Lists.newArrayList(playerMetadata.getInventory());
        }
        if (playerMetadata.lockerInventory != null) {
            this.lockerInventory = Lists.newArrayList(playerMetadata.getLockerInventory());
        }
        this.gold = new Long(playerMetadata.gold);
        this.goldInBank = new Long(playerMetadata.goldInBank);
        if (playerMetadata.playerRoleSet != null) {
            this.playerRoleSet = Sets.newConcurrentHashSet(playerMetadata.playerRoleSet);
        }
        if (playerMetadata.playerEquipment != null) {
            this.playerEquipment = Arrays.copyOf(playerMetadata.playerEquipment, playerMetadata.playerEquipment.length);
        }
        if (playerMetadata.effects != null) {
            this.effects = Lists.newArrayList(playerMetadata.getEffects());
        }
        if (playerMetadata.playerSettings != null) {
            this.playerSettings = new HashMap<>(playerMetadata.getPlayerSettings());
        }
        if (playerMetadata.learnedSpells != null) {
            this.learnedSpells = Arrays.copyOf(playerMetadata.learnedSpells, playerMetadata.learnedSpells.length);
        }
        this.isMarkedForDelete = new Boolean(playerMetadata.isMarkedForDelete);
        if (playerMetadata.npcKillLog != null) {
            this.npcKillLog = new HashMap<>(playerMetadata.getNpcKillLog());
        }
        if (playerMetadata.playerClass != null) {
            this.playerClass = playerMetadata.playerClass;
        }
        if (playerMetadata.coolDowns != null) {
            this.coolDowns = new ConcurrentHashMap<>(playerMetadata.coolDowns);
        }
        if (playerMetadata.currentRoomId != null) {
            this.currentRoomId = new Integer(playerMetadata.currentRoomId);
        }
        if (playerMetadata.acceptedQuests != null) {
            this.acceptedQuests = Maps.newHashMap(playerMetadata.acceptedQuests);
        }
        if (playerMetadata.completedQuests != null) {
            this.completedQuests = Sets.newLinkedHashSet(playerMetadata.completedQuests);
        }
    }

    public List<String> getInventory() {
        if (inventory == null) {
            inventory = Lists.newArrayList();
        }
        return inventory;
    }

    protected void addInventoryEntityId(String newEntityId) {
        if (inventory == null) {
            inventory = Lists.newArrayList();
        }
        inventory.add(newEntityId);
    }

    public List<String> getLockerInventory() {
        if (lockerInventory == null) {
            lockerInventory = Lists.newArrayList();
        }
        return lockerInventory;
    }

    protected void addLockerEntityId(String newEntityId) {
        if (lockerInventory == null) {
            lockerInventory = Lists.newArrayList();
        }
        lockerInventory.add(newEntityId);
    }

    protected void addNpcKill(String npcName) {
        if (this.npcKillLog == null) {
            npcKillLog = Maps.newHashMap();
        }
        if (npcKillLog.containsKey(npcName)) {
            Long aLong = npcKillLog.get(npcName);
            Long newLong = aLong + 1;
            npcKillLog.put(npcName, newLong);
        } else {
            npcKillLog.put(npcName, 1L);
        }
    }

    protected void addCoolDown(CoolDown coolDown) {
        if (this.coolDowns == null) {
            coolDowns = Maps.newConcurrentMap();
        }
        coolDowns.put(coolDown.getCoolDownType(), coolDown);
    }


    protected void removeLockerEntityId(String newEntityId) {
        lockerInventory.remove(newEntityId);
    }

    protected void removeInventoryEntityId(String itemId) {
        inventory.remove(itemId);
    }

    protected void addLearnedSpellByName(String spellName) {
        if (learnedSpells == null) {
            learnedSpells = new String[0];
        }
        String[] result = Arrays.copyOf(learnedSpells, learnedSpells.length + 1);
        result[learnedSpells.length] = spellName;
        this.learnedSpells = result;
    }

    protected void removeLearnedSpellByName(String spellName) {
        List<String> learnedSpellsKeep = new ArrayList<String>(Arrays.asList(learnedSpells));
        learnedSpellsKeep.remove(spellName);
        String[] newSpells = new String[learnedSpellsKeep.size()];
        int i = 0;
        for (String id : learnedSpellsKeep) {
            newSpells[i] = id;
            i++;
        }
        this.learnedSpells = newSpells;
    }

    protected void addEquipmentEntityId(String equipmentItemId) {
        if (playerEquipment == null) {
            playerEquipment = new String[0];
        }
        String[] result = Arrays.copyOf(playerEquipment, playerEquipment.length + 1);
        result[playerEquipment.length] = equipmentItemId;
        this.playerEquipment = result;
    }

    protected void removeEquipmentEntityId(String equipmentItemId) {
        List<String> equipMendItemsKeep = new ArrayList<String>(Arrays.asList(playerEquipment));
        equipMendItemsKeep.remove(equipmentItemId);
        String[] newItems = new String[equipMendItemsKeep.size()];
        int i = 0;
        for (String id : equipMendItemsKeep) {
            newItems[i] = id;
            i++;
        }
        this.playerEquipment = newItems;
    }

    protected void addEffect(Effect effect) {
        if (effects == null) {
            effects = Lists.newArrayList();
        }
        effects.add(effect);
    }

    public Integer getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(Integer currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    protected void removeEffect(Effect effect) {
        effects.remove(effect);
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlayerId() {
        return playerId;
    }

    public Stats getStats() {
        return stats;
    }

    public long getGold() {
        return gold;
    }

    public long getGoldInBank() {
        return goldInBank;
    }

    public String[] getLearnedSpells() {
        return learnedSpells;
    }

    public Map<String, Long> getNpcKillLog() {
        if (this.npcKillLog == null) {
            npcKillLog = Maps.newHashMap();
        }
        return npcKillLog;
    }

    protected void setGold(long amt) {
        this.gold = amt;
    }

    protected void setGoldInBank(long amt) {
        this.goldInBank = amt;
    }

    protected void incrementGold(long amt) {
        this.gold = gold + amt;
    }

    protected void transferGoldToBank(long amt) {
        this.gold = gold - amt;
        this.goldInBank = goldInBank + amt;
    }

    protected void transferBankGoldToPlayer(long amt) {
        this.goldInBank = goldInBank - amt;
        this.gold = gold + amt;
    }

    public Set<PlayerRole> getPlayerRoleSet() {
        return playerRoleSet;
    }

    public void addPlayerRole(PlayerRole playerRole) {
        if (this.playerRoleSet == null) {
            playerRoleSet = Sets.newHashSet();
        }
        playerRoleSet.add(playerRole);
    }

    public void resetPlayerRoles() {
        this.playerRoleSet = Sets.newConcurrentHashSet();
    }

    public void resetCoolDowns() {
        this.coolDowns = Maps.newConcurrentMap();
    }

    public String[] getPlayerEquipment() {
        return playerEquipment;
    }

    public List<Effect> getEffects() {
        if (effects == null) {
            effects = Lists.newArrayList();
        }
        return effects;
    }

    public boolean isMarkedForDelete() {
        return isMarkedForDelete;
    }

    public void setIsMarkedForDelete(boolean isMarkedForDelete) {
        this.isMarkedForDelete = isMarkedForDelete;
    }

    protected void resetEffects() {
        this.effects = Lists.newArrayList();
    }

    public boolean setSetting(String key, String value) {
        if (playerSettings == null) {
            playerSettings = Maps.newHashMap();
        }
        if (playerSettings.size() >= 100) {
            return false;
        }
        if (playerSettings == null) {
            this.playerSettings = Maps.newHashMap();
        }
        PlayerSettings byKey = PlayerSettings.getByKey(key);
        if (byKey == null) {
            return false;
        }
        if (byKey.getType().equals(Integer.TYPE)) {
            try {
                int i = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        playerSettings.put(key, value);
        return true;
    }

    public String getSetting(String key) {
        if (playerSettings == null) {
            playerSettings = Maps.newHashMap();
        }
        return playerSettings.get(key);
    }

    public Map<String, String> getPlayerSettings() {
        if (playerSettings == null) {
            playerSettings = Maps.newHashMap();
        }
        return playerSettings;
    }

    public void deleteSetting(String key) {
        playerSettings.remove(key);
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    public Set<CoolDown> getCoolDowns() {
        if (coolDowns == null) {
            coolDowns = Maps.newConcurrentMap();
        }
        return Sets.newHashSet(coolDowns.values());
    }

    public Map<CoolDownType, CoolDown> getCoolDownMap() {
        return coolDowns;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Set<String> getCompletedQuests() {
        if (this.completedQuests == null) {
            this.completedQuests = Sets.newLinkedHashSet();
        }
        return completedQuests;
    }

    public Map<String, Quest> getAcceptedQuests() {
        if (acceptedQuests == null) {
            this.acceptedQuests = Maps.newHashMap();
        }
        return acceptedQuests;
    }
}

